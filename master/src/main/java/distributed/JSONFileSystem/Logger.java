package distributed.JSONFileSystem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger is used by JSONDirManager to log specifics actions or errors. It can be also used to retrieve information
 * and solve syncronization issues, if any happens.
 * 
 * @see JSONDirManager
 * @author pdvass
 */
public class Logger {
    /**
     * Level can be used as a first filter to retrieve information from log files.
     */
    private static enum Level {
        DEFAULT,
        INFO,
        WARN,
        DANGER,
        TRANSACTION,
    };

    private final String path = "src/main/java/distributed/logs/";
    private Level level = Level.DEFAULT;
    private File f;

    public Logger(){
        this.f = new File(this.path + "master.log");
        try {
            if(!f.exists()){
                this.f.createNewFile();
            }
        } catch (IOException e){
            System.err.println("Problem creating log file: " + e.getMessage());
        }
    }

    public Logger(String filename){
        this.f = new File(this.path + filename + ".log");
        try {
            if(!f.exists()){
                this.f.createNewFile();
            }
        } catch (IOException e){
            System.err.println("Problem creating log file: " + e.getMessage());
        }
    }

    protected void setLevel(String level){
        switch (level) {
            case "info":
                this.level = Level.INFO;
                break;
            case "warn":
                this.level = Level.WARN;
                break;
            case "danger":
                this.level = Level.DANGER;
                break;
            case "transaction":
                this.level = Level.TRANSACTION;
                break;
            default:
                this.level = Level.DEFAULT;
                break;
        }
    }

    private String getLevelLabel(){
        String label = "";
        switch (this.level) {
            case Level.INFO:
                label = "[INFO]";
                break;
            case Level.WARN:
                label = "[WARNING]";
                break;
            case Level.DANGER:
                label = "[ERROR]";
                break;
            case Level.TRANSACTION:
                label = "[TRANSACTION]";
                break;
            default:
                System.err.println("Level should never be left at DEFAULT");
                break;
        }

        return label;
    }

    protected synchronized void writeToLog(String contents){
        String out = this.getLevelLabel();
        out += " " + contents;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        out += " " + dtf.format(now);
        try {
            FileWriter writer = new FileWriter(this.f, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(out);
            bufferedWriter.write("\n");
            bufferedWriter.close();
            writer.close();
        } catch (IOException e) {
            System.err.println("Could not write to logs. Reason: " + e.getMessage());
        } 
    }
    
}