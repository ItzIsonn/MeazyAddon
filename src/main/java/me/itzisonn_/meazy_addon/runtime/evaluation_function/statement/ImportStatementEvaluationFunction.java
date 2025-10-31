package me.itzisonn_.meazy_addon.runtime.evaluation_function.statement;

import me.itzisonn_.meazy.FileUtils;
import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.parser.ast.Program;
import me.itzisonn_.meazy.runtime.InvalidFileException;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.parser.ast.statement.ImportStatement;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.util.List;

public class ImportStatementEvaluationFunction extends AbstractEvaluationFunction<ImportStatement> {
    public ImportStatementEvaluationFunction() {
        super("import_statement");
    }

    @Override
    public RuntimeValue<?> evaluate(ImportStatement importStatement, RuntimeContext context, Environment environment, Object... extra) {
        if (!(environment instanceof FileEnvironment fileEnvironment)) {
            throw new RuntimeException("Can't use imports in non-global environment");
        }

        String folderPath = fileEnvironment.getParentFile().getParentFile().getAbsolutePath() + "\\";
        File file = new File(folderPath + importStatement.getFile());
        if (file.isDirectory() || !file.exists()) {
            throw new InvalidFileException("File '" + file.getAbsolutePath() + "' doesn't exist");
        }

        String extension = FileUtils.getExtension(file);
        Program program;
        switch (extension) {
            case "mea" -> {
                List<Token> tokens = Registries.TOKENIZATION_FUNCTION.getEntry().getValue().tokenize(FileUtils.getLines(file));
                program = Registries.PARSE_TOKENS_FUNCTION.getEntry().getValue().parse(file, tokens);
            }
            case "meac" -> {
                program = Registries.getGson().fromJson(FileUtils.getLines(file), Program.class);
                if (program == null) throw new InvalidFileException("Failed to read file '" + file.getAbsolutePath() + "'");
                if (MeazyMain.VERSION.isBefore(program.getVersion())) throw new InvalidFileException("Can't run file that has been compiled by a more recent version of the Meazy (" + program.getVersion() + "), in a more older version (" + MeazyMain.VERSION + ")");
                if (MeazyMain.VERSION.isAfter(program.getVersion())) {
                    MeazyMain.LOGGER.log(Level.WARN, Text.translatable("meazy_addon:runtime.unsafe", program.getVersion(), MeazyMain.VERSION));
                }
                program.setFile(file);
            }
            default -> throw new InvalidFileException("Can't run file with extension " + extension);
        }

        GlobalEnvironment globalEnvironment = context.getGlobalEnvironment();
        globalEnvironment.addFileEnvironment(Registries.EVALUATE_PROGRAM_FUNCTION.getEntry().getValue().evaluate(program, globalEnvironment));
        return null;
    }
}
