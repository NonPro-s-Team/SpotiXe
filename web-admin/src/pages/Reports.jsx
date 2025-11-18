import { FileText, Plus } from 'lucide-react'
import Button from '@components/common/Button'
import Card from '@components/common/Card'
import EmptyState from '@components/common/EmptyState'

export default function Reports() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-text-primary mb-2">Reports & Insights</h1>
          <p className="text-text-secondary">Generate and view custom reports</p>
        </div>
        <Button icon={Plus}>Create Report</Button>
      </div>

      <Card>
        <EmptyState
          icon={FileText}
          title="No reports yet"
          description="Create your first report to analyze your data"
          action={<Button icon={Plus}>Create Report</Button>}
        />
      </Card>
    </div>
  )
}
