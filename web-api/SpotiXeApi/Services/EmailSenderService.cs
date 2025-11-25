using System.Net;
using System.Net.Mail;
using System.Threading.Tasks;

namespace SpotiXeApi.Services
{
    public class EmailSenderService
    {
        private readonly IConfiguration _config;

        public EmailSenderService(IConfiguration config)
        {
            _config = config;
        }

        public async Task SendAsync(string to, string subject, string body)
        {
            var smtpHost = _config["Email:SmtpHost"];
            var smtpPort = int.Parse(_config["Email:SmtpPort"]);
            var smtpUser = _config["Email:Username"];
            var smtpPass = _config["Email:Password"];
            var fromEmail = _config["Email:From"];

            var message = new MailMessage();
            message.From = new MailAddress(fromEmail);
            message.To.Add(to);
            message.Subject = subject;
            message.Body = body;

            var client = new SmtpClient(smtpHost, smtpPort)
            {
                Credentials = new NetworkCredential(smtpUser, smtpPass),
                EnableSsl = true
            };

            await client.SendMailAsync(message);
        }
    }
}
