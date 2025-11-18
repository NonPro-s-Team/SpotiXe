import { DollarSign, TrendingUp, Download } from 'lucide-react'
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts'
import Card, { CardHeader, CardTitle, CardContent } from '@components/common/Card'
import StatCard from '@components/dashboard/StatCard'
import Button from '@components/common/Button'
import { formatCurrency } from '@utils/helpers'

const revenueData = Array.from({ length: 12 }, (_, i) => ({
  month: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'][i],
  revenue: Math.floor(Math.random() * 100000) + 300000,
}))

export default function Revenue() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-text-primary mb-2">Revenue Dashboard</h1>
          <p className="text-text-secondary">Track your platform's financial performance</p>
        </div>
        <Button icon={Download} variant="secondary">Export Report</Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <StatCard title="Total Revenue" value={formatCurrency(4567890)} subtitle="All time" icon={DollarSign} trendValue={12.5} />
        <StatCard title="MRR" value={formatCurrency(456789)} subtitle="Monthly recurring" icon={TrendingUp} trendValue={8.3} gradient="blue" />
        <StatCard title="This Month" value={formatCurrency(387654)} subtitle="Current month" icon={DollarSign} trendValue={5.2} gradient="purple" />
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Revenue Trend (Last 12 Months)</CardTitle>
        </CardHeader>
        <CardContent>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={revenueData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#282828" />
              <XAxis dataKey="month" stroke="#6A6A6A" tick={{ fill: '#B3B3B3' }} />
              <YAxis stroke="#6A6A6A" tick={{ fill: '#B3B3B3' }} />
              <Tooltip />
              <Line type="monotone" dataKey="revenue" stroke="#1DB954" strokeWidth={3} />
            </LineChart>
          </ResponsiveContainer>
        </CardContent>
      </Card>
    </div>
  )
}
