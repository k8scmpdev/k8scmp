class DefaultMailSender {
    public Object send(String number, String subject, String content) throws Exception {
        return ["fromAddr": "745640582@qq.com",
                "host": "mail.qq.com ",
                "number": number,
                "subject": subject,
                "content": content]
    }

    public Object send() throws Exception {
        return ["fromAddr": "745640582@qq.com",
                "host": "mail.qq.com"]
    }
}