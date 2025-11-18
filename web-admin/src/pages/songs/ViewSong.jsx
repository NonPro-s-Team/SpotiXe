import { useState, useEffect, useRef } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { motion } from 'framer-motion'
import toast from 'react-hot-toast'
import {
  Music,
  Check,
  ArrowLeft,
  Play,
  Pause,
  Save,
  Trash2,
} from 'lucide-react'
import Button from '@components/common/Button'
import Card from '@components/common/Card'
import ConfirmDialog from '@components/common/ConfirmDialog'
import { formatDuration } from '@utils/helpers'
import { getSongById, deleteSong } from '@services/api/songService'
import { useConfirmDialog } from '@hooks/useConfirmDialog'

// Read-only View Song page (previously mode === 'view')
export default function ViewSong() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)
  const [song, setSong] = useState(null)
  const [coverPreview, setCoverPreview] = useState(null)
  const [audioPreview, setAudioPreview] = useState(null)
  const [isPlaying, setIsPlaying] = useState(false)
  const [currentTime, setCurrentTime] = useState(0)
  const [audioDuration, setAudioDuration] = useState(0)
  const audioRef = useRef(null)

  const { isOpen: isDeleteDialogOpen, data: songToDelete, openDialog: openDeleteDialog, closeDialog: closeDeleteDialog } = useConfirmDialog()

  useEffect(() => {
    const load = async () => {
      if (!id) return
      setIsLoading(true)
      try {
        const data = await getSongById(id)
        setSong(data)
        if (data.coverImageUrl) setCoverPreview(data.coverImageUrl)
        if (data.audioFileUrl) setAudioPreview(data.audioFileUrl)
      } catch { toast.error('Không thể tải dữ liệu bài hát') } finally { setIsLoading(false) }
    }
    load()
  }, [id])

  const togglePlayPause = () => { if (audioRef.current) { if (isPlaying) audioRef.current.pause(); else audioRef.current.play(); setIsPlaying(!isPlaying) } }
  const handleTimeUpdate = () => { if (audioRef.current) setCurrentTime(audioRef.current.currentTime) }
  const handleLoadedMetadata = () => { if (audioRef.current) setAudioDuration(audioRef.current.duration) }
  const handleSeek = (e) => { const t = parseFloat(e.target.value); if (audioRef.current) { audioRef.current.currentTime = t; setCurrentTime(t) } }
  const handleAudioEnded = () => { setIsPlaying(false); setCurrentTime(0) }

  const handleDelete = () => { if (!song) return; openDeleteDialog({ id, title: song.title || 'bài hát này' }) }
  const handleConfirmDelete = async () => {
    try { await deleteSong(id); toast.success('Xóa bài hát thành công!'); navigate('/songs') } catch { toast.error('Không thể xóa bài hát') } finally { closeDialogDeleteSafe() }
  }
  const closeDialogDeleteSafe = () => { closeDeleteDialog() }

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div className="h-8 bg-bg-secondary rounded animate-pulse w-64" />
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <Card className="p-8 space-y-6">{[1,2,3,4,5,6].map(i => <div key={i} className="h-16 bg-bg-secondary rounded animate-pulse" />)}</Card>
          <Card className="p-8 space-y-6"><div className="h-64 bg-bg-secondary rounded animate-pulse" /><div className="h-32 bg-bg-secondary rounded animate-pulse" /></Card>
        </div>
      </div>
    )
  }

  if (!song) return null

  const formattedDate = song.releaseDate ? (() => { const d = new Date(song.releaseDate); const year = d.getFullYear(); const month = String(d.getMonth()+1).padStart(2,'0'); const day = String(d.getDate()).padStart(2,'0'); return `${day}/${month}/${year}` })() : null

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between mb-6">
        <nav className="flex items-center gap-2 text-sm text-text-secondary">
          <button onClick={() => navigate('/songs')} className="p-2 hover:bg-bg-hover rounded-lg transition-colors"><ArrowLeft className="w-5 h-5" /></button>
          <button onClick={() => navigate('/songs')} className="hover:text-text-primary">Quản lý bài hát</button>
          <span>/</span><span className="text-text-primary">Chi tiết bài hát</span>
        </nav>
        <div className="flex gap-3">
          <Button variant="secondary" icon={Trash2} onClick={handleDelete} className="text-apple-red text-sm transition-colors">Xóa</Button>
          <Button icon={Save} onClick={() => navigate(`/songs/${id}/edit`)}>Chỉnh sửa</Button>
        </div>
      </div>
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <motion.div initial={{ opacity:0, x:-20 }} animate={{ opacity:1, x:0 }} transition={{ duration:0.3 }}>
          <Card className="p-8">
            <h2 className="text-xl font-semibold text-text-primary mb-6">Song Information</h2>
            <div className="space-y-6">
              <div><label className="block text-sm font-medium text-text-primary mb-2">Title</label><div className="px-4 py-3 bg-bg-secondary rounded-lg text-text-primary border border-border">{song.title || '-'}</div></div>
              <div><label className="block text-sm font-medium text-text-primary mb-2">Duration</label><div className="px-4 py-3 bg-bg-secondary rounded-lg text-text-primary border border-border">{song.duration ? formatDuration(parseInt(song.duration)) : '-'}</div></div>
              <div><label className="block text-sm font-medium text-text-primary mb-2">Release Date</label><div className="px-4 py-3 bg-bg-secondary rounded-lg text-text-primary border border-border">{formattedDate || '-'}</div></div>
              <div><label className="block text-sm font-medium text-text-primary mb-2">Genre</label><div className="px-4 py-3 bg-bg-secondary rounded-lg text-text-primary border border-border">{song.genre || '-'}</div></div>
              <div><label className="block text-sm font-medium text-text-primary mb-2">Artist</label><div className="px-4 py-3 bg-bg-secondary rounded-lg text-text-primary border border-border">{song.artistName || '-'}</div></div>
              <div><label className="block text-sm font-medium text-text-primary mb-2">Album</label><div className="px-4 py-3 bg-bg-secondary rounded-lg text-text-primary border border-border">{song.albumTitle || 'Không nằm trong album'}</div></div>
              <div>
                <label className="flex items-center justify-between">
                  <div><span className="text-sm font-medium text-text-primary">Publish Song</span><p className="text-xs text-text-tertiary mt-1">Make this song visible to users</p></div>
                  <div className="relative opacity-60">
                    <div className={`w-11 h-6 rounded-full ${song.isActive ? 'bg-spotify-green' : 'bg-bg-tertiary'}`}></div>
                    <div className={`absolute left-1 top-1 w-4 h-4 bg-white rounded-full transition-transform ${song.isActive ? 'translate-x-5' : ''}`}></div>
                  </div>
                </label>
              </div>
            </div>
          </Card>
        </motion.div>
        <motion.div initial={{ opacity:0, x:20 }} animate={{ opacity:1, x:0 }} transition={{ duration:0.3, delay:0.1 }} className="space-y-6">
          <Card className="p-8">
            <h2 className="text-xl font-semibold text-text-primary mb-6">Cover Image</h2>
            {coverPreview && <div className="space-y-4"><img src={coverPreview} alt="Cover preview" className="w-full aspect-square object-cover rounded-lg" /><div className="text-sm text-text-tertiary break-all bg-bg-secondary p-3 rounded"><strong>URL:</strong> {song.coverImageUrl}</div></div>}
          </Card>
          <Card className="p-8">
            <h2 className="text-xl font-semibold text-text-primary mb-6">Audio File</h2>
            {audioPreview && (
              <div className="space-y-4">
                <audio ref={audioRef} src={audioPreview} onTimeUpdate={handleTimeUpdate} onLoadedMetadata={handleLoadedMetadata} onEnded={handleAudioEnded} />
                <div className="bg-bg-secondary rounded-lg p-6">
                  <div className="flex items-center gap-4 mb-4">
                    <button type="button" onClick={togglePlayPause} className="w-12 h-12 rounded-full bg-spotify-green flex items-center justify-center hover:bg-spotify-green-dark transition-colors flex-shrink-0">{isPlaying ? <Pause className="w-6 h-6 text-white" /> : <Play className="w-6 h-6 text-white ml-0.5" />}</button>
                    <div className="flex-1">
                      <p className="text-text-primary font-medium">{song.title || 'Audio Preview'}</p>
                      <p className="text-text-tertiary text-sm">{formatDuration(Math.floor(currentTime))} / {formatDuration(Math.floor(audioDuration))}</p>
                    </div>
                    <Music className="w-8 h-8 text-spotify-green flex-shrink-0" />
                  </div>
                  <div className="w-full">
                    <input type="range" min="0" max={audioDuration || 0} value={currentTime} onChange={handleSeek} className="w-full h-1 bg-bg-tertiary rounded-full appearance-none cursor-pointer [&::-webkit-slider-thumb]:appearance-none [&::-webkit-slider-thumb]:w-3 [&::-webkit-slider-thumb]:h-3 [&::-webkit-slider-thumb]:rounded-full [&::-webkit-slider-thumb]:bg-spotify-green [&::-moz-range-thumb]:w-3 [&::-moz-range-thumb]:h-3 [&::-moz-range-thumb]:rounded-full [&::-moz-range-thumb]:bg-spotify-green [&::-moz-range-thumb]:border-0" style={{ background: `linear-gradient(to right, #1DB954 0%, #1DB954 ${(currentTime / audioDuration) * 100}%, #374151 ${(currentTime / audioDuration) * 100}%, #374151 100%)` }} />
                  </div>
                </div>
                <div className="text-sm text-text-tertiary break-all bg-bg-secondary p-3 rounded"><strong>URL:</strong> {song.audioFileUrl}</div>
              </div>
            )}
          </Card>
        </motion.div>
      </div>
      <motion.div initial={{ opacity:0, y:20 }} animate={{ opacity:1, y:0 }} transition={{ duration:0.3, delay:0.2 }} className="mt-6">
        <Card className="p-8">
          <h2 className="text-xl font-semibold text-text-primary mb-6">Song Preview</h2>
          <div className="flex items-center gap-6 p-6 bg-bg-secondary rounded-lg">
            <div className="w-24 h-24 rounded-lg bg-bg-tertiary flex items-center justify-center overflow-hidden flex-shrink-0">{coverPreview ? <img src={coverPreview} alt="Cover" className="w-full h-full object-cover" /> : <Music className="w-10 h-10 text-text-tertiary" />}</div>
            <div className="flex-1 min-w-0">
              <h3 className="text-lg font-semibold text-text-primary truncate">{song.title || 'Tên bài hát'}</h3>
              <p className="text-text-secondary">{song.artistName || 'Tên nghệ sĩ'}</p>
              <div className="flex items-center gap-4 mt-2 text-sm text-text-tertiary">
                {song.genre && <span className="px-2 py-1 bg-bg-tertiary rounded">{song.genre}</span>}
                {song.duration && <span>{formatDuration(parseInt(song.duration))}</span>}
                {formattedDate && <span>{formattedDate}</span>}
                {song.isActive && <span className="flex items-center gap-1 text-spotify-green"><Check className="w-4 h-4" /> Active</span>}
              </div>
            </div>
          </div>
        </Card>
      </motion.div>
      <ConfirmDialog isOpen={isDeleteDialogOpen} onClose={closeDialogDeleteSafe} onConfirm={handleConfirmDelete} itemName="bài hát" message={songToDelete ? (<div><p className="text-text-secondary mb-2">Bạn có chắc chắn muốn xóa bài hát <strong className="text-text-primary">&ldquo;{songToDelete.title}&rdquo;</strong>?</p><p className="text-red-500 text-sm">Hành động này không thể hoàn tác!</p></div>) : null} />
    </div>
  )
}
