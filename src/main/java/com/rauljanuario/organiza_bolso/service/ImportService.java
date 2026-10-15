package com.rauljanuario.organiza_bolso.service;

import com.rauljanuario.organiza_bolso.dto.import_dto.ImportResponseDTO;
import com.rauljanuario.organiza_bolso.enums.FormatType;
import com.rauljanuario.organiza_bolso.enums.StatusType;
import com.rauljanuario.organiza_bolso.exception.ImportException;
import com.rauljanuario.organiza_bolso.exception.UserNotFoundException;
import com.rauljanuario.organiza_bolso.model.CategoryRule;
import com.rauljanuario.organiza_bolso.model.StatementImport;
import com.rauljanuario.organiza_bolso.model.Transaction;
import com.rauljanuario.organiza_bolso.model.User;
import com.rauljanuario.organiza_bolso.repository.CategoryRuleRepository;
import com.rauljanuario.organiza_bolso.repository.StatementImportRepository;
import com.rauljanuario.organiza_bolso.repository.TransactionRepository;
import com.rauljanuario.organiza_bolso.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class ImportService {

    private static final DateTimeFormatter OFX_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final StatementImportRepository statementImportRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRuleRepository categoryRuleRepository;
    private final UserRepository userRepository;

    public ImportService(
            StatementImportRepository statementImportRepository,
            TransactionRepository transactionRepository,
            CategoryRuleRepository categoryRuleRepository,
            UserRepository userRepository
    ) {
        this.statementImportRepository = statementImportRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRuleRepository = categoryRuleRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ImportResponseDTO importStatement(MultipartFile file) {
        if (file == null || file.isEmpty() || file.getOriginalFilename() == null) {
            throw new ImportException("A non-empty CSV or OFX file is required");
        }

        User user = getAuthenticatedUser();
        String fileName = file.getOriginalFilename();
        FormatType format = detectFormat(fileName);
        List<ImportedTransaction> importedTransactions;
        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            importedTransactions = format == FormatType.CSV
                    ? parseCsv(content)
                    : parseOfx(content);
        } catch (IOException exception) {
            throw new ImportException("Could not read the statement file", exception);
        }

        StatementImport statementImport = new StatementImport();
        statementImport.setFileName(fileName);
        statementImport.setFormat(format);
        statementImport.setStatus(StatusType.PROCESSING);
        statementImport.setImportedAt(LocalDateTime.now());
        statementImport.setUser(user);
        StatementImport savedImport = statementImportRepository.save(statementImport);

        List<CategoryRule> rules = categoryRuleRepository.findByCategory_User_Id(user.getId()).stream()
                .sorted(Comparator.comparing(CategoryRule::getPriority))
                .toList();
        List<Transaction> transactions = importedTransactions.stream()
                .map(item -> toTransaction(item, user, savedImport, rules))
                .toList();
        transactionRepository.saveAll(transactions);

        savedImport.setStatus(StatusType.COMPLETED);
        statementImportRepository.save(savedImport);
        return new ImportResponseDTO(
                savedImport.getId(),
                savedImport.getFileName(),
                savedImport.getFormat(),
                savedImport.getStatus(),
                savedImport.getImportedAt(),
                transactions.size()
        );
    }

    @Transactional(readOnly = true)
    public List<ImportResponseDTO> getImports() {
        User user = getAuthenticatedUser();
        return statementImportRepository.findByUser_IdOrderByImportedAtDesc(user.getId()).stream()
                .map(statementImport -> new ImportResponseDTO(
                        statementImport.getId(),
                        statementImport.getFileName(),
                        statementImport.getFormat(),
                        statementImport.getStatus(),
                        statementImport.getImportedAt(),
                        transactionRepository.countByStatementImport_Id(statementImport.getId())
                ))
                .toList();
    }

    private Transaction toTransaction(
            ImportedTransaction item,
            User user,
            StatementImport statementImport,
            List<CategoryRule> rules
    ) {
        Transaction transaction = new Transaction();
        transaction.setDescription(item.description());
        transaction.setAmount(item.amount());
        transaction.setDate(item.date().atStartOfDay());
        transaction.setCategory(rules.stream()
                .filter(rule -> item.description().toLowerCase(Locale.ROOT)
                        .contains(rule.getKeyword().toLowerCase(Locale.ROOT)))
                .map(CategoryRule::getCategory)
                .findFirst()
                .orElse(null));
        transaction.setStatementImport(statementImport);
        transaction.setUser(user);
        transaction.setManuallyCategorized(false);
        transaction.setCreatedAt(LocalDateTime.now());
        return transaction;
    }

    private FormatType detectFormat(String fileName) {
        String lowerName = fileName.toLowerCase(Locale.ROOT);
        if (lowerName.endsWith(".csv")) {
            return FormatType.CSV;
        }
        if (lowerName.endsWith(".ofx")) {
            return FormatType.OFX;
        }
        throw new ImportException("Only CSV and OFX files are supported");
    }

    private List<ImportedTransaction> parseCsv(String content) {
        List<String> lines = content.lines().filter(line -> !line.isBlank()).toList();
        if (lines.size() < 2) {
            throw new ImportException("CSV must contain a header and at least one transaction");
        }
        char delimiter = lines.getFirst().contains(";") ? ';' : ',';
        List<String> headers = splitCsvLine(lines.getFirst(), delimiter).stream()
                .map(this::normalize)
                .toList();
        int descriptionIndex = findHeader(headers, "description", "descricao", "name", "nome", "memo");
        int amountIndex = findHeader(headers, "amount", "valor", "value");
        int dateIndex = findHeader(headers, "transactiondate", "date", "data", "dtposted");
        List<ImportedTransaction> result = new ArrayList<>();
        for (int lineNumber = 1; lineNumber < lines.size(); lineNumber++) {
            List<String> values = splitCsvLine(lines.get(lineNumber), delimiter);
            if (values.size() <= Math.max(descriptionIndex, Math.max(amountIndex, dateIndex))) {
                throw new ImportException("Invalid CSV row " + (lineNumber + 1));
            }
            result.add(new ImportedTransaction(
                    required(values.get(descriptionIndex), "description", lineNumber + 1),
                    parseAmount(values.get(amountIndex), lineNumber + 1),
                    parseDate(values.get(dateIndex), lineNumber + 1)
            ));
        }
        return result;
    }

    private List<ImportedTransaction> parseOfx(String content) {
        List<ImportedTransaction> result = new ArrayList<>();
        String[] entries = content.split("(?i)<STMTTRN>");
        for (int index = 1; index < entries.length; index++) {
            String entry = entries[index];
            String description = firstTag(entry, "NAME", "MEMO");
            String amount = firstTag(entry, "TRNAMT");
            String date = firstTag(entry, "DTPOSTED");
            if (description == null || amount == null || date == null) {
                throw new ImportException("Invalid OFX transaction");
            }
            result.add(new ImportedTransaction(description, parseAmount(amount, index), parseDate(date, index)));
        }
        if (result.isEmpty()) {
            throw new ImportException("OFX does not contain transactions");
        }
        return result;
    }

    private String firstTag(String entry, String... tags) {
        for (String tag : tags) {
            String value = entry.replaceFirst("(?is).*?<" + tag + "[^>]*>([^<\\r\\n]+).*", "$1").trim();
            if (!value.equals(entry.trim()) && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private List<String> splitCsvLine(String line, char delimiter) {
        List<String> values = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean quoted = false;
        for (char character : line.toCharArray()) {
            if (character == '"') {
                quoted = !quoted;
            } else if (character == delimiter && !quoted) {
                values.add(value.toString().trim());
                value.setLength(0);
            } else {
                value.append(character);
            }
        }
        values.add(value.toString().trim());
        return values;
    }

    private int findHeader(List<String> headers, String... accepted) {
        for (String header : accepted) {
            int index = headers.indexOf(header);
            if (index >= 0) {
                return index;
            }
        }
        throw new ImportException("CSV is missing a required column");
    }

    private String normalize(String value) {
        return value.replace("\uFEFF", "").replaceAll("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT);
    }

    private String required(String value, String field, int line) {
        if (value == null || value.isBlank()) {
            throw new ImportException("Missing " + field + " on row " + line);
        }
        return value.trim();
    }

    private BigDecimal parseAmount(String value, int line) {
        try {
            String normalized = value.trim().replace(" ", "");
            if (normalized.contains(",") && normalized.contains(".")) {
                normalized = normalized.lastIndexOf(',') > normalized.lastIndexOf('.')
                        ? normalized.replace(".", "").replace(',', '.')
                        : normalized.replace(",", "");
            } else {
                normalized = normalized.replace(',', '.');
            }
            return new BigDecimal(normalized);
        } catch (NumberFormatException exception) {
            throw new ImportException("Invalid amount on transaction " + line, exception);
        }
    }

    private LocalDate parseDate(String value, int line) {
        String normalized = value.trim();
        try {
            if (normalized.matches("\\d{8}.*")) {
                return LocalDate.parse(normalized.substring(0, 8), OFX_DATE);
            }
            return LocalDate.parse(normalized, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException exception) {
            try {
                return LocalDate.parse(normalized, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (DateTimeParseException ignored) {
                throw new ImportException("Invalid date on transaction " + line, exception);
            }
        }
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new UserNotFoundException("User not authenticated");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private record ImportedTransaction(String description, BigDecimal amount, LocalDate date) {
    }
}
