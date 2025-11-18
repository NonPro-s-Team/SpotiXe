import { ListMusic, Plus } from 'lucide-react'
import Button from '@components/common/Button'
import Card from '@components/common/Card'
import EmptyState from '@components/common/EmptyState'

export default function Playlists() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-text-primary mb-2">Playlists Management</h1>
          <p className="text-text-secondary">Manage featured and user-generated playlists</p>
        </div>
        <Button icon={Plus}>Create Playlist</Button>
      </div>

      <Card>
        <EmptyState
          icon={ListMusic}
          title="No playlists yet"
          description="Create your first playlist to get started"
          action={<Button icon={Plus}>Create Playlist</Button>}
        />
      </Card>
    </div>
  )
}
