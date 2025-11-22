using Microsoft.EntityFrameworkCore;
using SpotiXeApi.Context;
using SpotiXeApi.Entities;

namespace SpotiXeApi.Repositories;

/// <summary>
/// Repository để thao tác với bảng Users
/// </summary>
public class UserRepository : IUserRepository
{
    private readonly SpotiXeDbContext _context;

    public UserRepository(SpotiXeDbContext context)
    {
        _context = context;
    }

    /// <summary>
    /// Tìm user theo Firebase UID
    /// </summary>
    public async Task<User?> FindByFirebaseUidAsync(string firebaseUid)
    {
        return await _context.Users
            .FirstOrDefaultAsync(u => u.FirebaseUid == firebaseUid && u.IsActive == 1);
    }

    /// <summary>
    /// Tìm user theo Email
    /// </summary>
    public async Task<User?> FindByEmailAsync(string email)
    {
        return await _context.Users
            .FirstOrDefaultAsync(u => u.Email == email && u.IsActive == 1);
    }

    /// <summary>
    /// Tìm user theo Phone Number
    /// </summary>
    public async Task<User?> FindByPhoneNumberAsync(string phoneNumber)
    {
        return await _context.Users
            .FirstOrDefaultAsync(u => u.PhoneNumber == phoneNumber && u.IsActive == 1);
    }

    /// <summary>
    /// Tìm user theo nhiều điều kiện (FirebaseUid, Email, hoặc Phone)
    /// Ưu tiên: FirebaseUid > Email > PhoneNumber
    /// </summary>
    public async Task<User?> FindByAnyIdentifierAsync(string? firebaseUid, string? email, string? phoneNumber)
    {
        // Tìm theo FirebaseUid trước
        if (!string.IsNullOrEmpty(firebaseUid))
        {
            var userByUid = await FindByFirebaseUidAsync(firebaseUid);
            if (userByUid != null) return userByUid;
        }

        // Nếu không tìm được, thử Email
        if (!string.IsNullOrEmpty(email))
        {
            var userByEmail = await FindByEmailAsync(email);
            if (userByEmail != null) return userByEmail;
        }

        // Cuối cùng thử PhoneNumber
        if (!string.IsNullOrEmpty(phoneNumber))
        {
            var userByPhone = await FindByPhoneNumberAsync(phoneNumber);
            if (userByPhone != null) return userByPhone;
        }

        return null;
    }

    /// <summary>
    /// Tạo user mới
    /// </summary>
    public async Task<User> CreateAsync(User user)
    {
        // Set giá trị mặc định
        user.IsActive = 1UL;
        user.CreatedAt = DateTime.UtcNow;
        user.UpdatedAt = DateTime.UtcNow;

        _context.Users.Add(user);
        await _context.SaveChangesAsync();
        return user;
    }

    /// <summary>
    /// Cập nhật user
    /// </summary>
    public async Task<User> UpdateAsync(User user)
    {
        user.UpdatedAt = DateTime.UtcNow;
        _context.Users.Update(user);
        await _context.SaveChangesAsync();
        return user;
    }

    /// <summary>
    /// Lấy user theo ID
    /// </summary>
    public async Task<User?> GetByIdAsync(long userId)
    {
        return await _context.Users
            .FirstOrDefaultAsync(u => u.UserId == userId && u.IsActive == 1);
    }
}
