import { useState, useEffect } from 'react'
import { useQuery } from '@tanstack/react-query'
import { format } from 'date-fns'
import { Users, DollarSign, Radio, TrendingUp } from 'lucide-react'
import { motion } from 'framer-motion'
import StatCard from '@components/dashboard/StatCard'
import { StreamActivityChart, TopGenresChart, UserDistributionChart } from '@components/dashboard/Charts'
import RecentActivity from '@components/dashboard/RecentActivity'
import TopTracks from '@components/dashboard/TopTracks'
import { CardSkeleton, TableSkeleton } from '@components/common/LoadingScreen'
import mockApi from '@services/mockApi'

export default function Dashboard() {
  const [currentTime, setCurrentTime] = useState(new Date())

  useEffect(() => {
    const timer = setInterval(() => setCurrentTime(new Date()), 60000)
    return () => clearInterval(timer)
  }, [])

  // Fetch dashboard data
  const { data: stats, isLoading: statsLoading } = useQuery({
    queryKey: ['dashboard-stats'],
    queryFn: mockApi.getDashboardStats,
  })

  const { data: streamActivity, isLoading: streamLoading } = useQuery({
    queryKey: ['stream-activity'],
    queryFn: mockApi.getStreamActivity,
  })

  const { data: topGenres, isLoading: genresLoading } = useQuery({
    queryKey: ['top-genres'],
    queryFn: mockApi.getTopGenres,
  })

  const { data: userDistribution, isLoading: distributionLoading } = useQuery({
    queryKey: ['user-distribution'],
    queryFn: mockApi.getUserDistribution,
  })

  const { data: recentActivities, isLoading: activitiesLoading } = useQuery({
    queryKey: ['recent-activities'],
    queryFn: mockApi.getRecentActivities,
  })

  const { data: topTracks, isLoading: tracksLoading } = useQuery({
    queryKey: ['top-tracks'],
    queryFn: mockApi.getTopTracks,
  })

  return (
    <div className="space-y-6">

      {/* KPI Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 md:gap-6">
        {statsLoading ? (
          <>
            <CardSkeleton />
            <CardSkeleton />
            <CardSkeleton />
            <CardSkeleton />
          </>
        ) : (
          <>
            <StatCard
              title="Tổng số người dùng"
              value={stats?.totalUsers}
              subtitle="Số người dùng đã đăng ký"
              icon={Users}
              trendValue={stats?.userGrowth}
              gradient="primary"
              delay={0}
              link="/users"
            />
            <StatCard
              title="Gói đăng ký hiện tại"
              value={stats?.activeSubscriptions}
              subtitle="Số người dùng trả phí"
              icon={TrendingUp}
              trendValue={stats?.subscriptionGrowth}
              gradient="purple"
              delay={0.1}
              link="/premium"
            />
            <StatCard
              title="Tổng lượt phát hôm nay"
              value={stats?.totalStreamsToday}
              subtitle="Theo thời gian thực"
              icon={Radio}
              trendValue={stats?.streamsGrowth}
              gradient="blue"
              delay={0.2}
              link="/analytics"
            />
            <StatCard
              title="Doanh thu tháng này"
              value={`$${(stats?.revenueThisMonth || 0).toLocaleString()}`}
              subtitle="So với tháng trước"
              icon={DollarSign}
              trendValue={stats?.revenueGrowth}
              gradient="orange"
              delay={0.3}
              link="/revenue"
            />
          </>
        )}
      </div>

      {/* Charts Row */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          {streamLoading ? (
            <CardSkeleton />
          ) : (
            <StreamActivityChart data={streamActivity || []} />
          )}
        </div>
        <div>
          {distributionLoading ? (
            <CardSkeleton />
          ) : (
            <UserDistributionChart data={userDistribution || []} />
          )}
        </div>
      </div>

      {/* Genres and Activities */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div>
          {genresLoading ? (
            <CardSkeleton />
          ) : (
            <TopGenresChart data={topGenres || []} />
          )}
        </div>
        <div>
          {activitiesLoading ? (
            <CardSkeleton />
          ) : (
            <RecentActivity activities={recentActivities || []} />
          )}
        </div>
      </div>

      {/* Top Tracks Table */}
      <div>
        {tracksLoading ? (
          <div className="card p-6">
            <TableSkeleton rows={5} columns={7} />
          </div>
        ) : (
          <TopTracks tracks={topTracks || []} />
        )}
      </div>
    </div>
  )
}
