using SpotiXeApi.Entities;

namespace SpotiXeApi.Repositories;

/// <summary>
/// Interface cho User Repository
/// </summary>
public interface IUserRepository
{
    /// <summary>
    /// Tìm user theo Firebase UID
    /// </summary>
    Task<User?> FindByFirebaseUidAsync(string firebaseUid);

    /// <summary>
    /// Tìm user theo Email
    /// </summary>
    Task<User?> FindByEmailAsync(string email);

    /// <summary>
    /// Tìm user theo Phone Number
    /// </summary>
    Task<User?> FindByPhoneNumberAsync(string phoneNumber);

    /// <summary>
    /// Tìm user theo nhiều điều kiện (FirebaseUid, Email, hoặc Phone)
    /// </summary>
    Task<User?> FindByAnyIdentifierAsync(string? firebaseUid, string? email, string? phoneNumber);

    /// <summary>
    /// Tạo user mới
    /// </summary>
    Task<User> CreateAsync(User user);

    /// <summary>
    /// Cập nhật user
    /// </summary>
    Task<User> UpdateAsync(User user);

    /// <summary>
    /// Lấy user theo ID
    /// </summary>
    Task<User?> GetByIdAsync(long userId);
}
