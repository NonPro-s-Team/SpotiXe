using Microsoft.EntityFrameworkCore;
using SpotiXeApi.Context;
using SpotiXeApi.Entities;

public class EmailOtpService
{
    private readonly SpotiXeDbContext _db;

    public EmailOtpService(SpotiXeDbContext db)
    {
        _db = db;
    }

    public async Task SaveOtpAsync(string email, string otp, DateTime expiresAt)
    {
        var entity = new Emailotp
        {
            Email = email,
            Otp = otp,
            ExpiresAt = expiresAt,
            CreatedAt = DateTime.UtcNow
        };

        _db.EmailOtps.Add(entity);
        await _db.SaveChangesAsync();
    }

    public async Task<Emailotp?> GetOtpAsync(string email)
    {
        return await _db.EmailOtps
            .Where(x => x.Email == email)
            .OrderByDescending(x => x.CreatedAt)
            .FirstOrDefaultAsync();
    }

    public async Task DeleteOtpAsync(long id)
    {
        var entity = await _db.EmailOtps.FindAsync(id);
        if (entity != null)
        {
            _db.EmailOtps.Remove(entity);
            await _db.SaveChangesAsync();
        }
    }
}
