using System;
using System.Net;
using System.Net.Mail;
using System.Threading.Tasks;

class Program
{
    static async Task Main(string[] args)
    {
        Console.WriteLine("Sending test email...");

        var message = new MailMessage();
        message.From = new MailAddress("noreply@spotixe.io.vn");
        message.To.Add("hungtranthien1@gmail.com");
        message.Subject = "Test mail làm otp";
        message.Body = "Đjt cụ nhà mày from anh Hien fan a độ mimi with nove";

        var smtp = new SmtpClient("smtp.zoho.com", 587)
        {
            Credentials = new NetworkCredential("noreply@spotixe.io.vn", "fhGBQqhWQV3k"),
            EnableSsl = true
        };

        try
        {
            await smtp.SendMailAsync(message);
            Console.WriteLine("Email sent successfully!");
        }
        catch (Exception ex)
        {
            Console.WriteLine("FAILED: " + ex.Message);
        }
    }
}
