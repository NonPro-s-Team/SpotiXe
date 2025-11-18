import { LineChart, Line, AreaChart, Area, BarChart, Bar, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts'
import Card, { CardHeader, CardTitle, CardContent } from '@components/common/Card'
import { formatNumber } from '@utils/helpers'

const COLORS = ['#1DB954', '#0A84FF', '#BF5AF2', '#FF6B35', '#FFD23F', '#FF10F0', '#00D9FF', '#FA243C']

const CustomTooltip = ({ active, payload, label }) => {
  if (active && payload && payload.length) {
    return (
      <div className="bg-bg-secondary border border-border rounded-lg p-3 shadow-xl">
        <p className="text-sm text-text-secondary mb-2">{label}</p>
        {payload.map((entry, index) => (
          <p key={index} className="text-sm font-medium" style={{ color: entry.color }}>
            {entry.name}: {formatNumber(entry.value)}
          </p>
        ))}
      </div>
    )
  }
  return null
}

export function StreamActivityChart({ data }) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Số lượt sử dụng (30 Ngày qua)</CardTitle>
      </CardHeader>
      <CardContent>
        <ResponsiveContainer width="100%" height={300}>
          <AreaChart data={data}>
            <defs>
              <linearGradient id="colorStreams" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor="#1DB954" stopOpacity={0.3}/>
                <stop offset="95%" stopColor="#1DB954" stopOpacity={0}/>
              </linearGradient>
              <linearGradient id="colorUsers" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor="#0A84FF" stopOpacity={0.3}/>
                <stop offset="95%" stopColor="#0A84FF" stopOpacity={0}/>
              </linearGradient>
            </defs>
            <CartesianGrid strokeDasharray="3 3" stroke="#282828" />
            <XAxis
              dataKey="date"
              stroke="#6A6A6A"
              tick={{ fill: '#B3B3B3', fontSize: 12 }}
              tickFormatter={(value) => new Date(value).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
            />
            <YAxis
              stroke="#6A6A6A"
              tick={{ fill: '#B3B3B3', fontSize: 12 }}
              tickFormatter={(value) => formatNumber(value)}
            />
            <Tooltip content={<CustomTooltip />} />
            <Legend wrapperStyle={{ color: '#B3B3B3' }} />
            <Area
              type="monotone"
              dataKey="streams"
              stroke="#1DB954"
              strokeWidth={2}
              fillOpacity={1}
              fill="url(#colorStreams)"
              name="Lượt phát"
            />
            <Area
              type="monotone"
              dataKey="uniqueUsers"
              stroke="#0A84FF"
              strokeWidth={2}
              fillOpacity={1}
              fill="url(#colorUsers)"
              name="Người dùng duy nhất"
            />
          </AreaChart>
        </ResponsiveContainer>
      </CardContent>
    </Card>
  )
}

export function TopGenresChart({ data }) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Top Genres Distribution</CardTitle>
      </CardHeader>
      <CardContent>
        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={data}>
            <CartesianGrid strokeDasharray="3 3" stroke="#282828" />
            <XAxis
              dataKey="name"
              stroke="#6A6A6A"
              tick={{ fill: '#B3B3B3', fontSize: 12 }}
            />
            <YAxis
              stroke="#6A6A6A"
              tick={{ fill: '#B3B3B3', fontSize: 12 }}
              tickFormatter={(value) => formatNumber(value)}
            />
            <Tooltip content={<CustomTooltip />} />
            <Bar dataKey="value" fill="#1DB954" radius={[8, 8, 0, 0]} name="Streams">
              {data.map((entry, index) => (
                <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
              ))}
            </Bar>
          </BarChart>
        </ResponsiveContainer>
      </CardContent>
    </Card>
  )
}

export function UserDistributionChart({ data }) {
  const renderLabel = (entry) => {
    return `${entry.percentage}%`
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Phân bổ người dùng</CardTitle>
      </CardHeader>
      <CardContent>
        <ResponsiveContainer width="100%" height={300}>
          <PieChart>
            <Pie
              data={data}
              cx="50%"
              cy="50%"
              labelLine={false}
              label={renderLabel}
              outerRadius={100}
              fill="#8884d8"
              dataKey="value"
            >
              {data.map((entry, index) => (
                <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
              ))}
            </Pie>
            <Tooltip content={<CustomTooltip />} />
            <Legend wrapperStyle={{ color: '#B3B3B3' }} />
          </PieChart>
        </ResponsiveContainer>
        
        <div className="mt-6 grid grid-cols-2 gap-4">
          {data.map((item, index) => (
            <div key={item.name} className="flex items-center gap-3">
              <div
                className="w-4 h-4 rounded-full"
                style={{ backgroundColor: COLORS[index % COLORS.length] }}
              />
              <div>
                <p className="text-sm font-medium text-text-primary">{item.name}</p>
                <p className="text-xs text-text-secondary">{formatNumber(item.value)} users</p>
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  )
}
