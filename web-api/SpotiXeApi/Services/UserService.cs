using SpotiXeApi.DTOs;
using SpotiXeApi.Entities;
using SpotiXeApi.Repositories;

namespace SpotiXeApi.Services;

/// <summary>
/// Service xử lý logic liên quan đến User và Authentication
/// </summary>
public class UserService
{
    private readonly IUserRepository _userRepository;
    private readonly ILogger<UserService> _logger;

    public UserService(IUserRepository userRepository, ILogger<UserService> logger)
    {
        _userRepository = userRepository;
        _logger = logger;
    }

    /// <summary>
    /// Tìm hoặc tạo user từ thông tin Firebase
    /// </summary>
    /// <param name="firebaseUserInfo">Thông tin từ Firebase</param>
    /// <returns>User entity</returns>
    public async Task<User> FindOrCreateUserAsync(FirebaseUserInfo firebaseUserInfo)
    {
        // Tìm user theo FirebaseUid, Email, hoặc PhoneNumber
        var existingUser = await _userRepository.FindByAnyIdentifierAsync(
            firebaseUserInfo.Uid,
            firebaseUserInfo.Email,
            firebaseUserInfo.PhoneNumber
        );

        if (existingUser != null)
        {
            // User đã tồn tại - cập nhật thông tin nếu cần
            _logger.LogInformation($"User found with ID: {existingUser.UserId}");
            return await UpdateUserFromFirebaseAsync(existingUser, firebaseUserInfo);
        }
        else
        {
            // Tạo user mới
            _logger.LogInformation($"Creating new user with Firebase UID: {firebaseUserInfo.Uid}");
            return await CreateUserFromFirebaseAsync(firebaseUserInfo);
        }
    }

    /// <summary>
    /// Tạo user mới từ thông tin Firebase
    /// </summary>
    private async Task<User> CreateUserFromFirebaseAsync(FirebaseUserInfo firebaseUserInfo)
    {
        var newUser = new User
        {
            FirebaseUid = firebaseUserInfo.Uid,
            Email = firebaseUserInfo.Email,
            PhoneNumber = firebaseUserInfo.PhoneNumber,
            Username = GenerateUsername(firebaseUserInfo),
            AvatarUrl = firebaseUserInfo.PhotoUrl,
            IsActive = true,
            CreatedAt = DateTime.UtcNow,
            UpdatedAt = DateTime.UtcNow,
            CreatedByName = "System",
            UpdatedByName = "System"
        };

        return await _userRepository.CreateAsync(newUser);
    }

    /// <summary>
    /// Cập nhật user từ thông tin Firebase (nếu có thông tin mới)
    /// </summary>
    private async Task<User> UpdateUserFromFirebaseAsync(User existingUser, FirebaseUserInfo firebaseUserInfo)
    {
        bool needUpdate = false;

        // Cập nhật FirebaseUid nếu chưa có hoặc khác
        if (string.IsNullOrEmpty(existingUser.FirebaseUid) || existingUser.FirebaseUid != firebaseUserInfo.Uid)
        {
            existingUser.FirebaseUid = firebaseUserInfo.Uid;
            needUpdate = true;
        }

        // Cập nhật Email nếu chưa có
        if (string.IsNullOrEmpty(existingUser.Email) && !string.IsNullOrEmpty(firebaseUserInfo.Email))
        {
            existingUser.Email = firebaseUserInfo.Email;
            needUpdate = true;
        }

        // Cập nhật PhoneNumber nếu chưa có
        if (string.IsNullOrEmpty(existingUser.PhoneNumber) && !string.IsNullOrEmpty(firebaseUserInfo.PhoneNumber))
        {
            existingUser.PhoneNumber = firebaseUserInfo.PhoneNumber;
            needUpdate = true;
        }

        // Cập nhật Username nếu chưa có
        if (string.IsNullOrEmpty(existingUser.Username))
        {
            existingUser.Username = GenerateUsername(firebaseUserInfo);
            needUpdate = true;
        }

        // Cập nhật Avatar nếu chưa có
        if (string.IsNullOrEmpty(existingUser.AvatarUrl) && !string.IsNullOrEmpty(firebaseUserInfo.PhotoUrl))
        {
            existingUser.AvatarUrl = firebaseUserInfo.PhotoUrl;
            needUpdate = true;
        }

        // Cập nhật UpdatedByName
        existingUser.UpdatedByName = "System";

        if (needUpdate)
        {
            _logger.LogInformation($"Updating user {existingUser.UserId} with new Firebase info");
            return await _userRepository.UpdateAsync(existingUser);
        }

        return existingUser;
    }

    /// <summary>
    /// Tạo username từ thông tin Firebase
    /// </summary>
    private string GenerateUsername(FirebaseUserInfo firebaseUserInfo)
    {
        // Ưu tiên: DisplayName > Email prefix > Phone > UID
        if (!string.IsNullOrEmpty(firebaseUserInfo.DisplayName))
        {
            return firebaseUserInfo.DisplayName;
        }

        if (!string.IsNullOrEmpty(firebaseUserInfo.Email))
        {
            return firebaseUserInfo.Email.Split('@')[0];
        }

        if (!string.IsNullOrEmpty(firebaseUserInfo.PhoneNumber))
        {
            return $"User_{firebaseUserInfo.PhoneNumber.Replace("+", "").Replace(" ", "")}";
        }

        return $"User_{firebaseUserInfo.Uid.Substring(0, 8)}";
    }

    /// <summary>
    /// Map User entity sang UserDto
    /// </summary>
    public UserDto MapToDto(User user)
    {
        return new UserDto
        {
            UserId = user.UserId,
            Email = user.Email,
            Phone = user.PhoneNumber,
            Username = user.Username,
            AvatarUrl = user.AvatarUrl,
            FirebaseUid = user.FirebaseUid
        };
    }
}
