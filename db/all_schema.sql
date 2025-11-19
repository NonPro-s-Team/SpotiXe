CREATE DATABASE spotixe;
USE spotixe;
-- ==============================
-- 1. Bảng Users
-- ==============================
CREATE TABLE Users (
    UserId BIGINT AUTO_INCREMENT PRIMARY KEY,
    Username VARCHAR(100) NOT NULL,
    Email VARCHAR(255) UNIQUE NULL,
    PhoneNumber VARCHAR(20) NULL,
    FirebaseUid VARCHAR(100) UNIQUE NULL,             -- UID từ Firebase
    AvatarUrl VARCHAR(500) NULL,

    -- Soft delete + Audit
    IsActive BIT(1) NOT NULL DEFAULT 1,
    DeletedAt DATETIME NULL,
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CreatedById BIGINT NULL,
    CreatedByName VARCHAR(255) NULL,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    UpdatedById BIGINT NULL,
    UpdatedByName VARCHAR(255) NULL
);

-- ==============================
-- 2. Bảng Artists
-- ==============================
CREATE TABLE Artists (
    ArtistId BIGINT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Bio TEXT NULL,
    Country VARCHAR(100) NULL,
    ProfileImageUrl VARCHAR(500) NULL,

    -- Soft delete + Audit
    IsActive BIT(1) NOT NULL DEFAULT 1,
    DeletedAt DATETIME NULL,
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CreatedById BIGINT NULL,
    CreatedByName VARCHAR(255) NULL,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    UpdatedById BIGINT NULL,
    UpdatedByName VARCHAR(255) NULL
);

-- ==============================
-- 3. Bảng Albums
-- ==============================
CREATE TABLE Albums (
    AlbumId BIGINT AUTO_INCREMENT PRIMARY KEY,
    Title VARCHAR(255) NOT NULL,
    ArtistId BIGINT NOT NULL,                                         -- FK đến Artists
    CoverImageUrl VARCHAR(500) NULL,
    ReleaseDate DATE NULL,

    CONSTRAINT FK_Albums_Artists
        FOREIGN KEY (ArtistId)
        REFERENCES Artists(ArtistId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    -- Soft delete + Audit
    IsActive BIT(1) NOT NULL DEFAULT 1,
    DeletedAt DATETIME NULL,
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CreatedById BIGINT NULL,
    CreatedByName VARCHAR(255) NULL,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    UpdatedById BIGINT NULL,
    UpdatedByName VARCHAR(255) NULL
);

-- ==============================
-- 4. Bảng Songs
-- ==============================
CREATE TABLE Songs (
    SongId BIGINT AUTO_INCREMENT PRIMARY KEY,
    Title VARCHAR(255) NOT NULL,
    Duration INT NULL,                                               -- Thời lượng (giây)
    ReleaseDate DATE NULL,
    AudioFileUrl VARCHAR(500) NULL,
    CoverImageUrl VARCHAR(500) NULL,
    Genre VARCHAR(100) NULL,
    ArtistId BIGINT NOT NULL,
    AlbumId BIGINT NULL,

    CONSTRAINT FK_Songs_Artists
        FOREIGN KEY (ArtistId)
        REFERENCES Artists(ArtistId)
        ON DELETE CASCADE                                            -- Xoá Artist → Xoá Song
        ON UPDATE CASCADE,

    CONSTRAINT FK_Songs_Albums
        FOREIGN KEY (AlbumId)
        REFERENCES Albums(AlbumId)
        ON DELETE SET NULL                                           -- Xoá Album → giữ Song
        ON UPDATE CASCADE,

    -- Soft delete + Audit
    IsActive BIT(1) NOT NULL DEFAULT 1,
    DeletedAt DATETIME NULL,
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CreatedById BIGINT NULL,
    CreatedByName VARCHAR(255) NULL,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    UpdatedById BIGINT NULL,
    UpdatedByName VARCHAR(255) NULL
);

-- ==============================
-- 5. Bảng Playlists
-- ==============================
CREATE TABLE Playlists (
    PlaylistId BIGINT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Description TEXT NULL,
    CoverImageUrl VARCHAR(500) NULL,
    OwnerUserId BIGINT NOT NULL,                                     -- Người tạo playlist
    IsPublic BIT(1) DEFAULT 1,

    CONSTRAINT FK_Playlists_Users
        FOREIGN KEY (OwnerUserId)
        REFERENCES Users(UserId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    -- Soft delete + Audit
    IsActive BIT(1) NOT NULL DEFAULT 1,
    DeletedAt DATETIME NULL,
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CreatedById BIGINT NULL,
    CreatedByName VARCHAR(255) NULL,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    UpdatedById BIGINT NULL,
    UpdatedByName VARCHAR(255) NULL
);

-- ==============================
-- 6. Bảng PlaylistSongs (N:N)
-- ==============================
CREATE TABLE PlaylistSongs (
    PlaylistId BIGINT NOT NULL,
    SongId BIGINT NOT NULL,
    Position INT NULL,
    AddedAt DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT FK_PlaylistSongs_Playlists
        FOREIGN KEY (PlaylistId)
        REFERENCES Playlists(PlaylistId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT FK_PlaylistSongs_Songs
        FOREIGN KEY (SongId)
        REFERENCES Songs(SongId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    PRIMARY KEY (PlaylistId, SongId)
);

-- ==============================
-- 7. Bảng UserFollowedPlaylists (N:N)
-- ==============================
CREATE TABLE UserFollowedPlaylists (
    UserId BIGINT NOT NULL,
    PlaylistId BIGINT NOT NULL,
    FollowedAt DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT FK_UserFollowedPlaylists_Users
        FOREIGN KEY (UserId)
        REFERENCES Users(UserId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT FK_UserFollowedPlaylists_Playlists
        FOREIGN KEY (PlaylistId)
        REFERENCES Playlists(PlaylistId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    PRIMARY KEY (UserId, PlaylistId)
);
