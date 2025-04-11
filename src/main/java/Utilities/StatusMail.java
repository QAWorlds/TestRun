package Utilities;

import java.io.File;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javafx.application.Application;
import org.apache.log4j.Logger;
import TestLib.Automation_properties;

public class StatusMail {

    public final static String PROPERTYFILENAME = "config/mail.properties";
    private static Properties mailProperties = new Properties();
    final public static Logger logger = Logger.getLogger(StatusMail.class);

    public static int totalTCcount = 0;
    public static int passTCcount = 0;
    public static int failTCcount = 0;
    public static String testPassPercentage = null;
    public static String testPassRate = null;

    public static int SUMMARYFLAG;

    public static String subject = "Hydroflask Smoke Test Reports";
    public static String to = "";
    public static String cc = "raikantiaravind650@gmail.com";
    public static String attachmentPath = "";

    public static String getCleanedEmails(String rawEmails) {
        if (rawEmails == null || rawEmails.trim().isEmpty()) {
            return "raikantiaravind650@gmail.com";
        }
        return rawEmails.trim();
    }

    public static void sendMail() throws Exception {
        subject = "Hydroflask Smoke Test Reports";
        attachmentPath = HTMLPreparation.generateMail("exectionReport");

        PieChartGenerator pie = new PieChartGenerator();
        if (totalTCcount - failTCcount > 0) {
            pie.setTestPassed(totalTCcount - failTCcount);
        }
        if (failTCcount > 0) {
            pie.setTestFailed(failTCcount);
        }

        Application.launch(PieChartGenerator.class);
        triggerSendMail(); // recipient setup now happens inside this method
    }

    public static void sendHealthCheckReport() throws Exception {
        subject = "Health Check Report";
        attachmentPath = HTMLPreparation.generateMail("healthReport");
        triggerSendMail(); // recipient setup now happens inside this method
    }

    public static void determineRecipients() {
        String baseUrl = Automation_properties.getInstance().getProperty("BASEURL").toLowerCase();

        if (baseUrl.contains("oxo")) {
            to = "ramyasreebogi@gmail.com";
        } else if (baseUrl.contains("drybar")) {
            to = "drybar.team@example.com";
        } else if (baseUrl.contains("osprey")) {
            to = "raikantiaravind650@gmail.com";
        } else {
            to = getCleanedEmails(Automation_properties.getInstance().getProperty("ReportEmail"));
        }
    }

    public static void triggerSendMail() {
        determineRecipients(); // 👈 Ensures 'to' is always initialized

        String userName = "nagarajusada31@gmail.com";
        String passWord = "cjvo hipv mhkc uquu";

        String host = "smtp.gmail.com";
        String port = "465";

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.ssl.trust", host);
        props.put("mail.smtp.debug", "true");

        try {
            String extentReportDir = System.getProperty("user.dir") + "/testlogs/ExtentReport";
            String zipPath = System.getProperty("user.dir") + "/testlogs/ExtentReport.zip";
            ZipUtility.zipDirectory(extentReportDir, zipPath);

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(userName, passWord);
                }
            });

            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(userName));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to.trim()));
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc.trim()));
            msg.setSubject(subject);

            Multipart multipart = new MimeMultipart();

            // HTML Report
            MimeBodyPart htmlPart = new MimeBodyPart();
            DataSource source = new FileDataSource(attachmentPath);
            htmlPart.setDataHandler(new DataHandler(source));
            htmlPart.setFileName("ExecutionReport.html");
            multipart.addBodyPart(htmlPart);

            // Pie Chart
            MimeBodyPart imagePart = new MimeBodyPart();
            DataSource imageSource = new FileDataSource(System.getProperty("user.dir") + "/test-output/automationPichart.png");
            imagePart.setDataHandler(new DataHandler(imageSource));
            imagePart.setFileName("TestPieChart.png");
            imagePart.setHeader("Content-ID", "<image>");
            multipart.addBodyPart(imagePart);

            // Zipped ExtentReport
            MimeBodyPart extentZip = new MimeBodyPart();
            DataSource zipSource = new FileDataSource(zipPath);
            extentZip.setDataHandler(new DataHandler(zipSource));
            extentZip.setFileName("ExtentReport.zip");
            multipart.addBodyPart(extentZip);

            msg.setContent(multipart);
            Transport.send(msg);

            System.out.println("✅ Mail sent successfully using Gmail SMTP SSL (port 465)");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
