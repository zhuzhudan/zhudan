package com.study.bean;

import java.io.File;

public class Mail {
    // 邮件主题
    private String subject;

    // 邮件内容
    private String content;

    // 接收邮箱
    private String toAccount;

    // 附件
    private File attachmentFile;

    // 附件名
    private String attachmentFileName;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public File getAttachmentFile() {
        return attachmentFile;
    }

    public void setAttachmentFile(File attachmentFile) {
        this.attachmentFile = attachmentFile;
    }

    public String getAttachmentFileName() {
        return attachmentFileName;
    }

    public void setAttachmentFileName(String attachmentFileName) {
        this.attachmentFileName = attachmentFileName;
    }
}
