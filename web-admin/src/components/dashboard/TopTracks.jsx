import { Play, TrendingUp, TrendingDown } from 'lucide-react'
import Card, { CardHeader, CardTitle, CardContent } from '@components/common/Card'
import { formatNumber, formatDuration } from '@utils/helpers'
import { cn } from '@utils/helpers'

export default function TopTracks({ tracks }) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Top Performing Tracks</CardTitle>
      </CardHeader>
      <CardContent className="p-0">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-border">
                <th className="text-left px-4 py-3 text-xs font-medium text-text-tertiary uppercase tracking-wider">
                  #
                </th>
                <th className="text-left px-4 py-3 text-xs font-medium text-text-tertiary uppercase tracking-wider">
                  Track
                </th>
                <th className="text-left px-4 py-3 text-xs font-medium text-text-tertiary uppercase tracking-wider">
                  Artist
                </th>
                <th className="text-left px-4 py-3 text-xs font-medium text-text-tertiary uppercase tracking-wider">
                  Album
                </th>
                <th className="text-left px-4 py-3 text-xs font-medium text-text-tertiary uppercase tracking-wider">
                  Streams
                </th>
                <th className="text-left px-4 py-3 text-xs font-medium text-text-tertiary uppercase tracking-wider">
                  Duration
                </th>
                <th className="text-left px-4 py-3 text-xs font-medium text-text-tertiary uppercase tracking-wider">
                  Trend
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {tracks.map((track, index) => (
                <tr
                  key={track.id}
                  className="hover:bg-bg-hover transition-colors group"
                >
                  <td className="px-4 py-3">
                    <div className="flex items-center justify-center w-8 h-8 text-text-secondary group-hover:text-text-primary">
                      <span className="group-hover:hidden">{index + 1}</span>
                      <Play className="w-4 h-4 hidden group-hover:block" />
                    </div>
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex items-center gap-3">
                      <img
                        src={track.thumbnail}
                        alt={track.title}
                        className="w-10 h-10 rounded object-cover"
                      />
                      <span className="font-medium text-text-primary">
                        {track.title}
                      </span>
                    </div>
                  </td>
                  <td className="px-4 py-3 text-text-secondary">
                    {track.artist}
                  </td>
                  <td className="px-4 py-3 text-text-secondary">
                    {track.album}
                  </td>
                  <td className="px-4 py-3 text-text-primary font-medium">
                    {formatNumber(track.streams)}
                  </td>
                  <td className="px-4 py-3 text-text-secondary">
                    {formatDuration(track.duration)}
                  </td>
                  <td className="px-4 py-3">
                    <div className={cn(
                      'inline-flex items-center gap-1',
                      track.trend === 'up' ? 'text-spotify-green' : 'text-apple-red'
                    )}>
                      {track.trend === 'up' ? (
                        <TrendingUp className="w-4 h-4" />
                      ) : (
                        <TrendingDown className="w-4 h-4" />
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </CardContent>
    </Card>
  )
}
