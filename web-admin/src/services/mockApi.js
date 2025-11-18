import api from "./api";

/**
 * Mock data generator for development
 */
export const mockApi = {
  // Dashboard stats
  getDashboardStats: async () => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    return {
      totalUsers: 1245678,
      userGrowth: 12.5,
      activeSubscriptions: 345890,
      subscriptionGrowth: 8.3,
      totalStreamsToday: 8234567,
      streamsGrowth: 15.2,
      revenueThisMonth: 456789,
      revenueGrowth: 10.8,
    };
  },

  // Stream activity for charts (last 30 days)
  getStreamActivity: async () => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    const data = [];
    for (let i = 30; i >= 0; i--) {
      const date = new Date();
      date.setDate(date.getDate() - i);
      data.push({
        date: date.toISOString().split("T")[0],
        streams: Math.floor(Math.random() * 2000000) + 6000000,
        uniqueUsers: Math.floor(Math.random() * 500000) + 800000,
      });
    }
    return data;
  },

  // Top genres
  getTopGenres: async () => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    return [
      { name: "Pop", value: 3456789 },
      { name: "Hip Hop", value: 2987654 },
      { name: "Rock", value: 2345678 },
      { name: "Electronic", value: 1876543 },
      { name: "R&B", value: 1654321 },
      { name: "Country", value: 1234567 },
      { name: "Jazz", value: 987654 },
      { name: "Classical", value: 765432 },
    ];
  },

  // User distribution
  getUserDistribution: async () => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    return [
      { name: "Premium", value: 345890, percentage: 27.8 },
      { name: "Free", value: 899788, percentage: 72.2 },
    ];
  },

  // Recent activities
  getRecentActivities: async () => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    return [
      {
        id: "1",
        type: "user_signup",
        user: "John Doe",
        action: "signed up for Premium",
        timestamp: new Date(Date.now() - 5 * 60000).toISOString(),
      },
      {
        id: "2",
        type: "track_upload",
        user: "Artist Name",
        action: 'uploaded a new track "Summer Vibes"',
        timestamp: new Date(Date.now() - 15 * 60000).toISOString(),
      },
      {
        id: "3",
        type: "playlist_created",
        user: "Jane Smith",
        action: 'created playlist "Workout Mix"',
        timestamp: new Date(Date.now() - 30 * 60000).toISOString(),
      },
      {
        id: "4",
        type: "subscription",
        user: "Mike Johnson",
        action: "upgraded to Premium Plus",
        timestamp: new Date(Date.now() - 45 * 60000).toISOString(),
      },
      {
        id: "5",
        type: "artist_verified",
        user: "The Weeknd",
        action: "got verified",
        timestamp: new Date(Date.now() - 60 * 60000).toISOString(),
      },
    ];
  },

  // Top performing tracks
  // getTopTracks: async () => {
  //   await new Promise((resolve) => setTimeout(resolve, 500));
  //   return [
  //     {
  //       id: "1",
  //       title: "Blinding Lights",
  //       artist: "The Weeknd",
  //       album: "After Hours",
  //       streams: 2345678,
  //       duration: 200,
  //       thumbnail: "https://via.placeholder.com/50",
  //       trend: "up",
  //     },
  //     {
  //       id: "2",
  //       title: "Levitating",
  //       artist: "Dua Lipa",
  //       album: "Future Nostalgia",
  //       streams: 1987654,
  //       duration: 203,
  //       thumbnail: "https://via.placeholder.com/50",
  //       trend: "up",
  //     },
  //     {
  //       id: "3",
  //       title: "Save Your Tears",
  //       artist: "The Weeknd",
  //       album: "After Hours",
  //       streams: 1765432,
  //       duration: 215,
  //       thumbnail: "https://via.placeholder.com/50",
  //       trend: "down",
  //     },
  //     {
  //       id: "4",
  //       title: "Good 4 U",
  //       artist: "Olivia Rodrigo",
  //       album: "SOUR",
  //       streams: 1654321,
  //       duration: 178,
  //       thumbnail: "https://via.placeholder.com/50",
  //       trend: "up",
  //     },
  //     {
  //       id: "5",
  //       title: "Stay",
  //       artist: "The Kid LAROI & Justin Bieber",
  //       album: "Single",
  //       streams: 1543210,
  //       duration: 141,
  //       thumbnail: "https://via.placeholder.com/50",
  //       trend: "up",
  //     },
  //   ];
  // },

  // // Music tracks (paginated)
  // getTracks: async (page = 1, limit = 20, filters = {}) => {
  //   await new Promise((resolve) => setTimeout(resolve, 500));
  //   const total = 1250;
  //   const tracks = Array.from({ length: limit }, (_, i) => ({
  //     id: `track-${(page - 1) * limit + i + 1}`,
  //     title: `Track ${(page - 1) * limit + i + 1}`,
  //     artist: `Artist ${Math.floor(Math.random() * 100) + 1}`,
  //     album: `Album ${Math.floor(Math.random() * 50) + 1}`,
  //     duration: Math.floor(Math.random() * 180) + 120,
  //     uploadDate: new Date(
  //       Date.now() - Math.random() * 365 * 24 * 60 * 60 * 1000
  //     ).toISOString(),
  //     streams: Math.floor(Math.random() * 1000000),
  //     status: ["active", "pending", "approved"][Math.floor(Math.random() * 3)],
  //     genre: ["Pop", "Rock", "Hip Hop", "Electronic", "Jazz"][
  //       Math.floor(Math.random() * 5)
  //     ],
  //     thumbnail: "https://via.placeholder.com/50",
  //   }));

  //   return {
  //     data: tracks,
  //     total,
  //     page,
  //     totalPages: Math.ceil(total / limit),
  //   };
  // },

  // Artists
  getArtists: async (page = 1, limit = 12) => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    const total = 456;
    const artists = Array.from({ length: limit }, (_, i) => ({
      id: `artist-${(page - 1) * limit + i + 1}`,
      name: `Artist ${(page - 1) * limit + i + 1}`,
      verified: Math.random() > 0.5,
      followers: Math.floor(Math.random() * 1000000),
      totalStreams: Math.floor(Math.random() * 50000000),
      albums: Math.floor(Math.random() * 20) + 1,
      singles: Math.floor(Math.random() * 50) + 5,
      monthlyListeners: Math.floor(Math.random() * 5000000),
      avatar: `https://via.placeholder.com/150`,
      genre: ["Pop", "Rock", "Hip Hop", "Electronic", "Jazz"][
        Math.floor(Math.random() * 5)
      ],
    }));

    return {
      data: artists,
      total,
      page,
      totalPages: Math.ceil(total / limit),
    };
  },

  // Users
  getUsers: async (page = 1, limit = 20, filters = {}) => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    const total = 1245678;
    const users = Array.from({ length: limit }, (_, i) => ({
      id: `user-${(page - 1) * limit + i + 1}`,
      name: `User ${(page - 1) * limit + i + 1}`,
      email: `user${(page - 1) * limit + i + 1}@example.com`,
      avatar: Math.random() > 0.5 ? `https://via.placeholder.com/40` : null,
      plan: ["free", "premium", "premium-plus"][Math.floor(Math.random() * 3)],
      status: ["active", "inactive"][Math.floor(Math.random() * 2)],
      joinDate: new Date(
        Date.now() - Math.random() * 730 * 24 * 60 * 60 * 1000
      ).toISOString(),
      lastActive: new Date(
        Date.now() - Math.random() * 7 * 24 * 60 * 60 * 1000
      ).toISOString(),
    }));

    return {
      data: users,
      total,
      page,
      totalPages: Math.ceil(total / limit),
    };
  },

  // Analytics data
  getAnalyticsData: async (dateRange) => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    return {
      userGrowth: Array.from({ length: 30 }, (_, i) => ({
        date: new Date(Date.now() - (29 - i) * 24 * 60 * 60 * 1000)
          .toISOString()
          .split("T")[0],
        users: Math.floor(Math.random() * 5000) + 10000,
      })),
      deviceBreakdown: [
        { name: "Mobile", value: 456789 },
        { name: "Desktop", value: 345678 },
        { name: "Tablet", value: 123456 },
        { name: "Smart Speakers", value: 87654 },
      ],
      demographics: {
        age: [
          { range: "13-17", value: 123456 },
          { range: "18-24", value: 345678 },
          { range: "25-34", value: 456789 },
          { range: "35-44", value: 234567 },
          { range: "45-54", value: 123456 },
          { range: "55+", value: 87654 },
        ],
        gender: [
          { name: "Male", value: 54.3 },
          { name: "Female", value: 44.2 },
          { name: "Other", value: 1.5 },
        ],
      },
    };
  },
};

export default mockApi;
