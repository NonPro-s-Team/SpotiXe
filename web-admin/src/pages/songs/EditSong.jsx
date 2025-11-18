import { useState, useEffect, useRef } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { motion } from 'framer-motion'
import toast from 'react-hot-toast'
import { parseBlob } from 'music-metadata-browser'
import {
  Calendar,
  Music,
  Check,
  X,
  Save,
  ArrowLeft,
  Play,
  Pause,
  Trash2,
  Link as LinkIcon,
} from 'lucide-react'
import Button from '@components/common/Button'
import Card from '@components/common/Card'
import Input, { Select } from '@components/common/Input'
import ConfirmDialog from '@components/common/ConfirmDialog'
import { formatDuration } from '@utils/helpers'
import { getSongById, updateSong, deleteSong } from '@services/api/songService'
import { useArtists } from '@hooks/useArtists'
import { useAlbums } from '@hooks/useAlbums'
import { useConfirmDialog } from '@hooks/useConfirmDialog'

// Standalone Edit Song form (previously mode === 'edit' in unified SongForm)
export default function EditSong() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)
  const [coverPreview, setCoverPreview] = useState(null)
  const [audioPreview, setAudioPreview] = useState(null)
  const [isPlaying, setIsPlaying] = useState(false)
  const [currentTime, setCurrentTime] = useState(0)
  const [audioDuration, setAudioDuration] = useState(0)
  const [isExtractingMetadata, setIsExtractingMetadata] = useState(false)
  const audioRef = useRef(null)

  const [artistSearch, setArtistSearch] = useState('')
  const [showArtistDropdown, setShowArtistDropdown] = useState(false)
  const [selectedArtistName, setSelectedArtistName] = useState('')
  const [albumSearch, setAlbumSearch] = useState('')
  const [showAlbumDropdown, setShowAlbumDropdown] = useState(false)


  const { data: artistsData } = useArtists()
  const { data: albumsData } = useAlbums()

  const { isOpen: isDeleteDialogOpen, data: songToDelete, openDialog: openDeleteDialog, closeDialog: closeDeleteDialog } = useConfirmDialog()

  const { register, handleSubmit, watch, setValue, formState: { errors } } = useForm({
    defaultValues: {
      title: '', duration: '', releaseDate: '', genre: '', artistId: '', albumId: '', isActive: true, coverImageUrl: '', audioFileUrl: ''
    }
  })
  const watchedValues = watch()

  const genres = [
    { value: 'create-new', label: '+ Tạo thể loại mới', isSpecial: true },
    { value: 'Pop', label: 'Pop' },
    { value: 'Rock', label: 'Rock' },
    { value: 'Hip-Hop', label: 'Hip Hop' },
    { value: 'Jazz', label: 'Jazz' },
    { value: 'Electronic', label: 'Electronic' },
    { value: 'Classical', label: 'Classical' },
    { value: 'R&B', label: 'R&B' },
    { value: 'Country', label: 'Country' },
  ]
  const artists = [{ value: 'create-new', label: '+ Tạo nghệ sĩ mới', isSpecial: true }, ...(artistsData?.map(a => ({ value: a.artistId.toString(), label: a.name })) || [])]
  const albums = [{ value: 'create-new', label: '+ Tạo album mới', isSpecial: true }, ...(albumsData?.map(a => ({ value: a.albumId.toString(), label: a.title })) || [])]
  const filteredArtists = artists.filter(a => a.label.toLowerCase().includes(artistSearch.toLowerCase()))
  const filteredAlbums = albums.filter(a => a.label.toLowerCase().includes(albumSearch.toLowerCase()))

  // Load song data
  useEffect(() => {
    const load = async () => {
      if (!id) return
      setIsLoading(true)
      try {
        const songData = await getSongById(id)
        let formattedDate = ''
        if (songData.releaseDate) {
          const date = new Date(songData.releaseDate); formattedDate = date.toISOString().split('T')[0]
        }
        setValue('title', songData.title || '', { shouldValidate: false })
        setValue('duration', songData.duration ? songData.duration.toString() : '', { shouldValidate: false })
        setValue('releaseDate', formattedDate, { shouldValidate: false })
        setValue('genre', songData.genre || '', { shouldValidate: false })
        setValue('artistId', songData.artistId ? songData.artistId.toString() : '', { shouldValidate: false })
        setValue('albumId', songData.albumId ? songData.albumId.toString() : '', { shouldValidate: false })
        setValue('isActive', songData.isActive === 1 || songData.isActive === true, { shouldValidate: false })
        setValue('coverImageUrl', songData.coverImageUrl || '', { shouldValidate: false })
        setValue('audioFileUrl', songData.audioFileUrl || '', { shouldValidate: false })
        if (songData.artistName) { setArtistSearch(songData.artistName); setSelectedArtistName(songData.artistName) }
  if (songData.albumTitle) { setAlbumSearch(songData.albumTitle) }
  if (songData.albumTitle) { setAlbumSearch(songData.albumTitle) }
        if (songData.coverImageUrl) setCoverPreview(songData.coverImageUrl)
        if (songData.audioFileUrl) setAudioPreview(songData.audioFileUrl)
      } catch { toast.error('Không thể tải dữ liệu bài hát') } finally { setIsLoading(false) }
    }
    load()
  }, [id, setValue])

  useEffect(() => { if (watchedValues.coverImageUrl && !coverPreview) setCoverPreview(watchedValues.coverImageUrl) }, [watchedValues.coverImageUrl, coverPreview])
  useEffect(() => { if (watchedValues.audioFileUrl && !audioPreview) setAudioPreview(watchedValues.audioFileUrl) }, [watchedValues.audioFileUrl, audioPreview])

  const togglePlayPause = () => { if (audioRef.current) { if (isPlaying) audioRef.current.pause(); else audioRef.current.play(); setIsPlaying(!isPlaying) } }
  const handleTimeUpdate = () => { if (audioRef.current) setCurrentTime(audioRef.current.currentTime) }
  const handleLoadedMetadata = () => { if (audioRef.current) setAudioDuration(audioRef.current.duration) }
  const handleSeek = (e) => { const t = parseFloat(e.target.value); if (audioRef.current) { audioRef.current.currentTime = t; setCurrentTime(t) } }
  const handleAudioEnded = () => { setIsPlaying(false); setCurrentTime(0) }

  const extractMetadataFromUrl = async (url) => {
    if (!url || url.trim() === '') { toast.error('Vui lòng nhập Audio URL trước'); return }
    setIsExtractingMetadata(true); toast.loading('Đang trích xuất metadata từ audio...', { id: 'metadata' })
    try {
      const response = await fetch(url, { method: 'GET', headers: { 'Range': 'bytes=0-1048576' }, mode: 'cors' })
      if (!response.ok) throw new Error(`HTTP ${response.status}: Không thể tải file audio`)
      const blob = await response.blob(); if (blob.size === 0) throw new Error('File audio trống hoặc không thể tải')
      const metadata = await parseBlob(blob)
      const hasUsefulData = metadata.common?.title || metadata.common?.artist || metadata.common?.album || metadata.format?.duration
      if (!hasUsefulData) { toast.warning('File không chứa metadata.', { id: 'metadata' }); return }
      let fieldsUpdated = []
      if (metadata.common?.title && !watchedValues.title) { setValue('title', metadata.common.title, { shouldValidate: true }); fieldsUpdated.push('Title') }
      if (metadata.common?.artist && !watchedValues.artistId) {
        const artistName = metadata.common.artist; setArtistSearch(artistName)
        const foundArtist = artists.find(a => !a.isSpecial && a.label.toLowerCase() === artistName.toLowerCase())
        if (foundArtist) { setValue('artistId', foundArtist.value); setSelectedArtistName(foundArtist.label); fieldsUpdated.push('Artist') } else fieldsUpdated.push('Artist (tên)')
      }
      if (metadata.common?.album && !watchedValues.albumId) {
    const albumName = metadata.common.album; setAlbumSearch(albumName)
    const foundAlbum = albums.find(a => !a.isSpecial && a.label.toLowerCase() === albumName.toLowerCase())
    if (foundAlbum) { setValue('albumId', foundAlbum.value); fieldsUpdated.push('Album') } else fieldsUpdated.push('Album (tên)')
      }
      if (metadata.format?.duration && !watchedValues.duration) { setValue('duration', Math.floor(metadata.format.duration).toString()); fieldsUpdated.push('Duration') }
      if (metadata.common?.genre && metadata.common.genre.length > 0 && !watchedValues.genre) {
        const genreName = metadata.common.genre[0]; const foundGenre = genres.find(g => !g.isSpecial && g.label.toLowerCase() === genreName.toLowerCase()); if (foundGenre) { setValue('genre', foundGenre.value); fieldsUpdated.push('Genre') }
      }
      if (metadata.common?.year && !watchedValues.releaseDate) { setValue('releaseDate', `${metadata.common.year}-01-01`); fieldsUpdated.push('Release Year') }
      if (metadata.common?.picture && metadata.common.picture.length > 0 && !coverPreview) {
    try { const pic = metadata.common.picture[0]; const imgBlob = new Blob([pic.data], { type: pic.format }); const imgUrl = URL.createObjectURL(imgBlob); setCoverPreview(imgUrl); fieldsUpdated.push('Cover Image') } catch (imgErr) { console.warn('Failed to extract cover image', imgErr) }
      }
      if (fieldsUpdated.length > 0) toast.success(`Đã trích xuất: ${fieldsUpdated.join(', ')}`, { id: 'metadata', duration: 4000 })
      else toast.info('Các trường đã có dữ liệu', { id: 'metadata' })
    } catch (error) {
      if (error.name === 'TypeError' && error.message.includes('Failed to fetch')) toast.error('Lỗi CORS: Server không cho phép truy cập.', { id: 'metadata', duration: 6000 })
      else if (error.message.includes('HTTP 403') || error.message.includes('HTTP 401')) toast.error('Không có quyền truy cập file audio.', { id: 'metadata' })
      else if (error.message.includes('HTTP 404')) toast.error('Không tìm thấy file audio.', { id: 'metadata' })
      else toast.error('Không thể trích xuất metadata: ' + error.message, { id: 'metadata' })
    } finally { setIsExtractingMetadata(false) }
  }

  const onSubmit = async (data) => {
    if (!data.title?.trim()) { toast.error('Title is required'); return }
    if (!data.duration || parseInt(data.duration) < 1) { toast.error('Duration must be >= 1'); return }
    if (!data.coverImageUrl?.trim()) { toast.error('Cover image URL is required'); return }
    if (!data.audioFileUrl?.trim()) { toast.error('Audio file URL is required'); return }
    setIsLoading(true)
    try {
      await updateSong(id, {
        title: data.title,
        duration: parseInt(data.duration),
        releaseDate: data.releaseDate,
        genre: data.genre,
        artistId: parseInt(data.artistId),
        albumId: data.albumId ? parseInt(data.albumId) : null,
        isActive: data.isActive,
        coverImageUrl: data.coverImageUrl,
        audioFileUrl: data.audioFileUrl,
      })
      toast.success('Cập nhật bài hát thành công!')
      navigate('/songs')
    } catch { toast.error('Không thể cập nhật bài hát') } finally { setIsLoading(false) }
  }

  const handleDelete = () => { openDeleteDialog({ id, title: watchedValues.title || 'bài hát này' }) }
  const handleConfirmDelete = async () => {
    try { await deleteSong(id); toast.success('Xóa bài hát thành công!'); navigate('/songs') } catch { toast.error('Không thể xóa bài hát') } finally { closeDeleteDialog() }
  }

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

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <nav className="flex items-center gap-2 text-sm text-text-secondary">
          <button onClick={() => navigate('/songs')} className="p-2 hover:bg-bg-hover rounded-lg transition-colors"><ArrowLeft className="w-5 h-5" /></button>
          <button onClick={() => navigate('/songs')} className="hover:text-text-primary">Quản lý bài hát</button><span>/</span><span className="text-text-primary">Chỉnh sửa bài hát</span>
        </nav>
      </div>
      <form onSubmit={handleSubmit(onSubmit)}>
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Left */}
          <motion.div initial={{ opacity:0, x:-20 }} animate={{ opacity:1, x:0 }} transition={{ duration:0.3 }}>
            <Card className="p-8">
              <h2 className="text-xl font-semibold text-text-primary mb-6">Song Information</h2>
              <div className="space-y-6">
                <div>
                  <label className="block text-sm font-medium text-text-primary mb-2">Title <span className="text-red-500">*</span></label>
                  <Input value={watchedValues.title || ''} onChange={(e) => setValue('title', e.target.value, { shouldValidate: true })} placeholder="Enter song title" error={errors.title?.message} />
                </div>
                <div>
                  <label className="block text-sm font-medium text-text-primary mb-2">Duration <span className="text-red-500">*</span></label>
                  <Input type="number" value={watchedValues.duration || ''} onChange={(e) => setValue('duration', e.target.value, { shouldValidate: true })} placeholder="Duration in seconds" error={errors.duration?.message} />
                  {watchedValues.duration && <p className="text-xs text-text-tertiary mt-1">Preview: {formatDuration(parseInt(watchedValues.duration))}</p>}
                </div>
                <div>
                  <label className="block text-sm font-medium text-text-primary mb-2">Release Date</label>
                  <div className="relative">
                    <Input type="date" value={watchedValues.releaseDate || ''} onChange={(e) => setValue('releaseDate', e.target.value)} error={errors.releaseDate?.message} />
                    <Calendar className="absolute right-3 top-1/2 -translate-y-1/2 w-5 h-5 text-text-tertiary pointer-events-none" />
                    {watchedValues.releaseDate && <p className="text-xs text-text-tertiary mt-1">Preview: {(() => { const [y,m,d] = watchedValues.releaseDate.split('-'); return `${d}/${m}/${y}` })()}</p>}
                  </div>
                </div>
                <div>
                  <label className="block text-sm font-medium text-text-primary mb-2">Genre <span className="text-red-500">*</span></label>
                  <Select value={watchedValues.genre || ''} onChange={(e) => { if (e.target.value === 'create-new') { toast.info('Tính năng đang phát triển'); setValue('genre', '') } else setValue('genre', e.target.value) }} options={genres} error={errors.genre?.message} />
                </div>
                <div className="relative">
                  <label className="block text-sm font-medium text-text-primary mb-2">Artist <span className="text-red-500">*</span></label>
                  <Input type="text" value={artistSearch} onChange={(e) => { setArtistSearch(e.target.value); setShowArtistDropdown(true) }} onFocus={() => setShowArtistDropdown(true)} placeholder="Tìm kiếm hoặc nhập tên nghệ sĩ..." error={errors.artistId?.message} />
                  <input type="hidden" {...register('artistId', { required: 'Artist is required' })} />
                  {showArtistDropdown && (
                    <>
                      <div className="fixed inset-0 z-10" onClick={() => setShowArtistDropdown(false)} />
                      <div className="absolute z-20 w-full mt-1 bg-bg-card border border-border rounded-lg shadow-xl max-h-60 overflow-y-auto">
                        {filteredArtists.map(artist => (
                          <button key={artist.value} type="button" onClick={() => { if (artist.value === 'create-new') { toast.info('Chuyển đến trang tạo nghệ sĩ mới...') } else { setValue('artistId', artist.value); setArtistSearch(artist.label); setSelectedArtistName(artist.label); setShowArtistDropdown(false) } }} className={`w-full px-4 py-2 text-left hover:bg-bg-hover transition-colors ${artist.isSpecial ? 'text-spotify-green font-medium border-b border-border' : 'text-text-primary'}`}>{artist.label}</button>
                        ))}
                        {filteredArtists.length === 1 && filteredArtists[0].isSpecial && <div className="px-4 py-3 text-text-tertiary text-sm text-center">Không tìm thấy nghệ sĩ</div>}
                      </div>
                    </>
                  )}
                </div>
                <div className="relative">
                  <label className="block text-sm font-medium text-text-primary mb-2">Album <span className="text-text-tertiary">(Optional)</span></label>
                  <Input type="text" value={albumSearch} onChange={(e) => { setAlbumSearch(e.target.value); setShowAlbumDropdown(true) }} onFocus={() => setShowAlbumDropdown(true)} placeholder="Tìm kiếm hoặc nhập tên album..." />
                  <input type="hidden" {...register('albumId')} />
                  {showAlbumDropdown && (
                    <>
                      <div className="fixed inset-0 z-10" onClick={() => setShowAlbumDropdown(false)} />
                      <div className="absolute z-20 w-full mt-1 bg-bg-card border border-border rounded-lg shadow-xl max-h-60 overflow-y-auto">
                        {filteredAlbums.map(album => (
                          <button key={album.value} type="button" onClick={() => { if (album.value === 'create-new') { toast.info('Chuyển đến trang tạo album mới...'); navigate('/albums/add') } else { setValue('albumId', album.value); setAlbumSearch(album.label); setShowAlbumDropdown(false) } }} className={`w-full px-4 py-2 text-left hover:bg-bg-hover transition-colors ${album.isSpecial ? 'text-spotify-green font-medium border-b border-border' : 'text-text-primary'}`}>{album.label}</button>
                        ))}
                        {filteredAlbums.length === 1 && filteredAlbums[0].isSpecial && <div className="px-4 py-3 text-text-tertiary text-sm text-center">Không tìm thấy album</div>}
                      </div>
                    </>
                  )}
                </div>
                <div>
                  <label className="flex items-center justify-between cursor-pointer">
                    <div>
                      <span className="text-sm font-medium text-text-primary">Publish Song</span>
                      <p className="text-xs text-text-tertiary mt-1">Make this song visible to users</p>
                    </div>
                    <div className="relative">
                      <input type="checkbox" {...register('isActive')} className="sr-only peer" />
                      <div className="w-11 h-6 bg-bg-tertiary rounded-full peer peer-checked:bg-spotify-green transition-colors" />
                      <div className="absolute left-1 top-1 w-4 h-4 bg-white rounded-full transition-transform peer-checked:translate-x-5" />
                    </div>
                  </label>
                </div>
              </div>
            </Card>
          </motion.div>
          {/* Right */}
          <motion.div initial={{ opacity:0, x:20 }} animate={{ opacity:1, x:0 }} transition={{ duration:0.3, delay:0.1 }} className="space-y-6">
            <Card className="p-8">
              <h2 className="text-xl font-semibold text-text-primary mb-6">Cover Image <span className="text-red-500">*</span></h2>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-text-primary mb-2">Cover Image URL</label>
                  <div className="relative">
                    <Input type="url" value={watchedValues.coverImageUrl || ''} onChange={(e) => setValue('coverImageUrl', e.target.value)} placeholder="https://example.com/cover.jpg" error={errors.coverImageUrl?.message} />
                    <LinkIcon className="absolute right-3 top-1/2 -translate-y-1/2 w-5 h-5 text-text-tertiary pointer-events-none" />
                  </div>
                  <p className="text-xs text-text-tertiary mt-1">Nhập URL trực tiếp đến ảnh bìa (JPG, PNG, WebP, etc.)</p>
                </div>
                {coverPreview ? <img src={coverPreview} alt="Cover preview" className="w-full aspect-square object-cover rounded-lg" /> : watchedValues.coverImageUrl ? <Button type="button" variant="secondary" onClick={() => { setCoverPreview(watchedValues.coverImageUrl); toast.success('Đã tải image preview') }} className="w-full">Tải image preview</Button> : null}
              </div>
            </Card>
            <Card className="p-8">
              <h2 className="text-xl font-semibold text-text-primary mb-6">Audio File <span className="text-red-500">*</span></h2>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-text-primary mb-2">Audio URL</label>
                  <div className="relative">
                    <Input type="url" value={watchedValues.audioFileUrl || ''} onChange={(e) => setValue('audioFileUrl', e.target.value)} placeholder="https://example.com/audio.mp3" error={errors.audioFileUrl?.message} />
                    <LinkIcon className="absolute right-3 top-1/2 -translate-y-1/2 w-5 h-5 text-text-tertiary pointer-events-none" />
                  </div>
                  <p className="text-xs text-text-tertiary mt-1">Nhập URL trực tiếp đến file audio (MP3, WAV, FLAC, etc.)</p>
                </div>
                {watchedValues.audioFileUrl && <Button type="button" variant="secondary" onClick={() => extractMetadataFromUrl(watchedValues.audioFileUrl)} disabled={isExtractingMetadata} className="w-full">{isExtractingMetadata ? 'Đang trích xuất...' : 'Trích xuất metadata từ audio'}</Button>}
                {audioPreview && (
                  <>
                    <audio ref={audioRef} src={audioPreview} onTimeUpdate={handleTimeUpdate} onLoadedMetadata={handleLoadedMetadata} onEnded={handleAudioEnded} />
                    <div className="bg-bg-secondary rounded-lg p-6">
                      <div className="flex items-center gap-4 mb-4">
                        <button type="button" onClick={togglePlayPause} className="w-12 h-12 rounded-full bg-spotify-green flex items-center justify-center hover:bg-spotify-green-dark transition-colors flex-shrink-0">{isPlaying ? <Pause className="w-6 h-6 text-white" /> : <Play className="w-6 h-6 text-white ml-0.5" />}</button>
                        <div className="flex-1"><p className="text-text-primary font-medium">{watchedValues.title || 'Audio Preview'}</p><p className="text-text-tertiary text-sm">{formatDuration(Math.floor(currentTime))} / {formatDuration(Math.floor(audioDuration))}</p></div>
                        <Music className="w-8 h-8 text-spotify-green flex-shrink-0" />
                      </div>
                      <div className="w-full">
                        <input type="range" min="0" max={audioDuration || 0} value={currentTime} onChange={handleSeek} className="w-full h-1 bg-bg-tertiary rounded-full appearance-none cursor-pointer [&::-webkit-slider-thumb]:appearance-none [&::-webkit-slider-thumb]:w-3 [&::-webkit-slider-thumb]:h-3 [&::-webkit-slider-thumb]:rounded-full [&::-webkit-slider-thumb]:bg-spotify-green [&::-moz-range-thumb]:w-3 [&::-moz-range-thumb]:h-3 [&::-moz-range-thumb]:rounded-full [&::-moz-range-thumb]:bg-spotify-green [&::-moz-range-thumb]:border-0" style={{ background: `linear-gradient(to right, #1DB954 0%, #1DB954 ${(currentTime / audioDuration) * 100}%, #374151 ${(currentTime / audioDuration) * 100}%, #374151 100%)` }} />
                      </div>
                    </div>
                  </>
                )}
                {watchedValues.audioFileUrl && !audioPreview && <Button type="button" variant="secondary" onClick={() => { setAudioPreview(watchedValues.audioFileUrl); toast.success('Đã tải audio preview') }} className="w-full">Tải audio preview</Button>}
              </div>
            </Card>
          </motion.div>
        </div>
        <motion.div initial={{ opacity:0, y:20 }} animate={{ opacity:1, y:0 }} transition={{ duration:0.3, delay:0.2 }} className="mt-6">
          <Card className="p-8">
            <h2 className="text-xl font-semibold text-text-primary mb-6">Song Preview</h2>
            <div className="flex items-center gap-6 p-6 bg-bg-secondary rounded-lg">
              <div className="w-24 h-24 rounded-lg bg-bg-tertiary flex items-center justify-center overflow-hidden flex-shrink-0">{coverPreview ? <img src={coverPreview} alt="Cover" className="w-full h-full object-cover" /> : <Music className="w-10 h-10 text-text-tertiary" />}</div>
              <div className="flex-1 min-w-0">
                <h3 className="text-lg font-semibold text-text-primary truncate">{watchedValues.title || 'Tên bài hát'}</h3>
                <p className="text-text-secondary">{selectedArtistName || artistSearch || 'Tên nghệ sĩ'}</p>
                <div className="flex items-center gap-4 mt-2 text-sm text-text-tertiary">
                  {watchedValues.genre && <span className="px-2 py-1 bg-bg-tertiary rounded">{watchedValues.genre}</span>}
                  {watchedValues.duration && <span>{formatDuration(parseInt(watchedValues.duration))}</span>}
                  {watchedValues.releaseDate && <span>{(() => { const [y,m,d] = watchedValues.releaseDate.split('-'); return `${d}/${m}/${y}` })()}</span>}
                  {watchedValues.isActive && <span className="flex items-center gap-1 text-spotify-green"><Check className="w-4 h-4" /> Active</span>}
                </div>
              </div>
            </div>
          </Card>
        </motion.div>
        <motion.div initial={{ opacity:0, y:20 }} animate={{ opacity:1, y:0 }} transition={{ duration:0.3, delay:0.3 }} className="flex items-center justify-between mt-6">
          <div>
            <Button type="button" variant="danger" icon={Trash2} onClick={handleDelete}>Delete Song</Button>
          </div>
          <div className="flex gap-3">
            <Button type="button" variant="secondary" icon={X} onClick={() => navigate('/music')}>Cancel</Button>
            <Button type="submit" icon={Save} disabled={isLoading}>{isLoading ? 'Saving...' : 'Update Song'}</Button>
          </div>
        </motion.div>
      </form>
      <ConfirmDialog isOpen={isDeleteDialogOpen} onClose={closeDeleteDialog} onConfirm={handleConfirmDelete} itemName="bài hát" message={songToDelete ? (<div><p className="text-text-secondary mb-2">Bạn có chắc chắn muốn xóa bài hát <strong className="text-text-primary">&ldquo;{songToDelete.title}&rdquo;</strong>?</p><p className="text-red-500 text-sm">Hành động này không thể hoàn tác!</p></div>) : null} />
    </div>
  )
}
