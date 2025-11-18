import { formatDistanceToNow } from 'date-fns'
import { UserPlus, Music, ListMusic, Crown, CheckCircle } from 'lucide-react'
import Card, { CardHeader, CardTitle, CardContent } from '@components/common/Card'
import { cn } from '@utils/helpers'

const activityIcons = {
  user_signup: UserPlus,
  track_upload: Music,
  playlist_created: ListMusic,
  subscription: Crown,
  artist_verified: CheckCircle,
}

const activityColors = {
  user_signup: 'text-spotify-green bg-spotify-green/10',
  track_upload: 'text-purple bg-purple/10',
  playlist_created: 'text-apple-blue bg-apple-blue/10',
  subscription: 'text-accent-yellow bg-accent-yellow/10',
  artist_verified: 'text-accent-cyan bg-accent-cyan/10',
}

export default function RecentActivity({ activities }) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Recent Activities</CardTitle>
      </CardHeader>
      <CardContent className="p-0">
        <div className="divide-y divide-border">
          {activities.map((activity) => {
            const Icon = activityIcons[activity.type] || Music
            const colorClass = activityColors[activity.type] || 'text-text-secondary bg-bg-hover'
            
            return (
              <div
                key={activity.id}
                className="p-4 hover:bg-bg-hover transition-colors"
              >
                <div className="flex items-start gap-3">
                  <div className={cn('w-10 h-10 rounded-lg flex items-center justify-center', colorClass)}>
                    <Icon className="w-5 h-5" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm text-text-primary">
                      <span className="font-medium">{activity.user}</span>{' '}
                      {activity.action}
                    </p>
                    <p className="text-xs text-text-tertiary mt-1">
                      {formatDistanceToNow(new Date(activity.timestamp), { addSuffix: true })}
                    </p>
                  </div>
                </div>
              </div>
            )
          })}
        </div>
      </CardContent>
    </Card>
  )
}
