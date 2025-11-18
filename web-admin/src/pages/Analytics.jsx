import { useQuery } from '@tanstack/react-query'
import { Calendar, Download, TrendingUp, Users, Globe, Smartphone } from 'lucide-react'
import { LineChart, Line, BarChart, Bar, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend, AreaChart, Area } from 'recharts'
import Card, { CardHeader, CardTitle, CardContent } from '@components/common/Card'
import { Select } from '@components/common/Input'
import Button from '@components/common/Button'
import { CardSkeleton } from '@components/common/LoadingScreen'
import mockApi from '@services/mockApi'
import { formatNumber } from '@utils/helpers'

const COLORS = ['#1DB954', '#0A84FF', '#BF5AF2', '#FF6B35']

export default function Analytics() {
  const { data, isLoading } = useQuery({
    queryKey: ['analytics'],
    queryFn: mockApi.getAnalyticsData,
  })

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-text-primary mb-2">Analytics Dashboard</h1>
          <p className="text-text-secondary">Deep insights into your platform performance</p>
        </div>
        <div className="flex items-center gap-2">
          <Select
            options={[
              { value: '7', label: 'Last 7 days' },
              { value: '30', label: 'Last 30 days' },
              { value: '90', label: 'Last 90 days' },
            ]}
            className="w-40"
          />
          <Button icon={Download} variant="secondary">Export</Button>
        </div>
      </div>

      {isLoading ? (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {Array.from({ length: 4 }).map((_, i) => <CardSkeleton key={i} />)}
        </div>
      ) : (
        <>
          {/* User Growth Chart */}
          <Card>
            <CardHeader>
              <CardTitle>User Growth (Last 30 Days)</CardTitle>
            </CardHeader>
            <CardContent>
              <ResponsiveContainer width="100%" height={300}>
                <AreaChart data={data?.userGrowth}>
                  <defs>
                    <linearGradient id="colorGrowth" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#1DB954" stopOpacity={0.3}/>
                      <stop offset="95%" stopColor="#1DB954" stopOpacity={0}/>
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" stroke="#282828" />
                  <XAxis dataKey="date" stroke="#6A6A6A" tick={{ fill: '#B3B3B3' }} />
                  <YAxis stroke="#6A6A6A" tick={{ fill: '#B3B3B3' }} />
                  <Tooltip />
                  <Area type="monotone" dataKey="users" stroke="#1DB954" fillOpacity={1} fill="url(#colorGrowth)" />
                </AreaChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>

          {/* Device Breakdown & Demographics */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <Card>
              <CardHeader>
                <CardTitle>Device Breakdown</CardTitle>
              </CardHeader>
              <CardContent>
                <ResponsiveContainer width="100%" height={250}>
                  <PieChart>
                    <Pie data={data?.deviceBreakdown} cx="50%" cy="50%" outerRadius={80} dataKey="value" label>
                      {data?.deviceBreakdown.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Age Demographics</CardTitle>
              </CardHeader>
              <CardContent>
                <ResponsiveContainer width="100%" height={250}>
                  <BarChart data={data?.demographics.age}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#282828" />
                    <XAxis dataKey="range" stroke="#6A6A6A" tick={{ fill: '#B3B3B3' }} />
                    <YAxis stroke="#6A6A6A" tick={{ fill: '#B3B3B3' }} />
                    <Tooltip />
                    <Bar dataKey="value" fill="#1DB954" radius={[8, 8, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>
          </div>
        </>
      )}
    </div>
  )
}
