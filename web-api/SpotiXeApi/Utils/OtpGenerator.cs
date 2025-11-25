using System.Security.Cryptography;

namespace SpotiXeApi.Utils
{
    public class OtpGenerator
    {
        public static string GenerateOtp(int digits = 6)
        {
            // Max số lượng dựa trên số đơn vị
            int maxValue = (int)Math.Pow(10, digits) - 1;

            int value;

            using (var rng = RandomNumberGenerator.Create())
            {
                var bytes = new byte[4];
                rng.GetBytes(bytes);
                value = BitConverter.ToInt32(bytes, 0);

                // Chuyển về số dương
                value &= int.MaxValue;
            }

            // Giới hạn theo maxValue
            value = value % (maxValue + 1);

            // Trả về dạng 6 chữ số
            return value.ToString(new string('0', digits));
        }
    }
}
