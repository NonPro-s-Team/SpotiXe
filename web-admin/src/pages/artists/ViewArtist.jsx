import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { motion } from 'framer-motion'
import toast from 'react-hot-toast'
import { useQueryClient } from '@tanstack/react-query'
import { Image as ImageIcon, ArrowLeft, Trash2, Edit } from 'lucide-react'
import Button from '@components/common/Button'
import Card from '@components/common/Card'
import ConfirmDialog from '@components/common/ConfirmDialog'
import { useConfirmDialog } from '@hooks/useConfirmDialog'
import { getArtistById, deleteArtist } from '@services/api/artistService'

// Read-only artist view page
export default function ViewArtist() {
  const { id } = useParams()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { isOpen, openDialog, closeDialog, confirmDialog } = useConfirmDialog()
  const [artist, setArtist] = useState(null)
  const [isLoading, setIsLoading] = useState(true)
  const [isDeleting, setIsDeleting] = useState(false)

  useEffect(() => {
    const load = async () => {
      if (!id) return
      setIsLoading(true)
      try {
        const data = await getArtistById(id)
        console.log('Fetched artist data:', data) // Debug log
        setArtist(data)
      } catch (error) {
        console.error('Error loading artist:', error)
        toast.error('Không thể tải dữ liệu nghệ sĩ')
      } finally {
        setIsLoading(false)
      }
    }
    load()
  }, [id])

  const handleDeleteClick = () => {
    const artistName = artist?.name || artist?.artistName || 'nghệ sĩ này'
    openDialog({ id, name: artistName })
  }

  const handleDeleteConfirm = async () => {
    setIsDeleting(true)
    try {
      await deleteArtist(id)
      
      // Invalidate React Query cache to force refetch
      queryClient.invalidateQueries({ queryKey: ['artists'] })
      
      toast.success('Xóa nghệ sĩ thành công!')
      navigate('/artists')
    } catch (error) {
      console.error('Error deleting artist:', error)
      toast.error('Không thể xóa nghệ sĩ')
    } finally {
      setIsDeleting(false)
    }
  }

  // Loading skeleton
  if (isLoading) {
    return (
      <div className="space-y-6">
        <div className="h-8 bg-bg-secondary rounded animate-pulse w-64" />
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2 space-y-6">
            <Card className="p-6">
              <div className="space-y-4">
                <div className="h-8 bg-bg-secondary rounded animate-pulse w-48" />
                <div className="h-20 bg-bg-secondary rounded animate-pulse" />
                <div className="grid grid-cols-2 gap-4">
                  <div className="h-16 bg-bg-secondary rounded animate-pulse" />
                  <div className="h-16 bg-bg-secondary rounded animate-pulse" />
                </div>
              </div>
            </Card>
          </div>
          <div className="h-64 bg-bg-secondary rounded animate-pulse" />
        </div>
      </div>
    )
  }

  // No data found
  if (!artist) {
    return (
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <nav className="flex items-center gap-2 text-sm text-text-secondary">
            <button onClick={() => navigate('/artists')} className="p-2 hover:bg-bg-hover rounded-lg transition-colors">
              <ArrowLeft className="w-5 h-5" />
            </button>
            <button onClick={() => navigate('/artists')} className="hover:text-text-primary">Quản lý nghệ sĩ</button>
          </nav>
        </div>
        <Card className="p-12 text-center">
          <ImageIcon className="w-16 h-16 mx-auto mb-4 text-text-tertiary" />
          <h3 className="text-xl font-semibold mb-2">Không tìm thấy nghệ sĩ</h3>
          <p className="text-text-secondary mb-6">Nghệ sĩ này có thể đã bị xóa hoặc không tồn tại</p>
          <Button onClick={() => navigate('/artists')}>Quay lại danh sách</Button>
        </Card>
      </div>
    )
  }

  // Get artist name from either 'name' or 'artistName' field
  const artistName = artist.name || artist.artistName || 'Không có tên'

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <nav className="flex items-center gap-2 text-sm text-text-secondary">
          <button onClick={() => navigate('/artists')} className="p-2 hover:bg-bg-hover rounded-lg transition-colors">
            <ArrowLeft className="w-5 h-5" />
          </button>
          <button onClick={() => navigate('/artists')} className="hover:text-text-primary">Quản lý nghệ sĩ</button>
          <span>/</span>
          <span className="text-text-primary">{artistName}</span>
        </nav>
        <div className="flex gap-3">
          <Button variant="secondary" size="sm" onClick={() => navigate(`/artists/${id}/edit`)} icon={Edit}>Chỉnh sửa</Button>
          <Button variant="danger" size="sm" onClick={handleDeleteClick} icon={Trash2}>Xóa</Button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main info */}
        <div className="lg:col-span-2 space-y-6">
          <Card className="p-6">
            <h2 className="text-2xl font-bold mb-6">{artistName}</h2>
            
            <div className="space-y-6">
              {/* Bio */}
              {artist.bio && (
                <div>
                  <h3 className="text-sm font-medium text-text-secondary mb-2">Tiểu sử</h3>
                  <p className="text-text-primary whitespace-pre-wrap">{artist.bio}</p>
                </div>
              )}

              {/* Details grid */}
              <div className="grid grid-cols-2 gap-6">
                {artist.country && (
                  <div>
                    <h3 className="text-sm font-medium text-text-secondary mb-2">Quốc gia</h3>
                    <p className="text-text-primary">{artist.country}</p>
                  </div>
                )}
                
                {artist.genre && (
                  <div>
                    <h3 className="text-sm font-medium text-text-secondary mb-2">Thể loại</h3>
                    <p className="text-text-primary">{artist.genre}</p>
                  </div>
                )}
                
                {artist.debutYear && (
                  <div>
                    <h3 className="text-sm font-medium text-text-secondary mb-2">Năm ra mắt</h3>
                    <p className="text-text-primary">{artist.debutYear}</p>
                  </div>
                )}
              </div>
            </div>
          </Card>
        </div>

        {/* Artist image */}
        <div>
          <Card className="p-6">
            <h3 className="text-lg font-semibold mb-4">Ảnh nghệ sĩ</h3>
            <div className="aspect-square rounded-lg overflow-hidden bg-bg-secondary border border-border-secondary">
              {artist.profileImageUrl ? (
                <motion.img
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  src={artist.profileImageUrl}
                  alt={artistName}
                  className="w-full h-full object-cover"
                  onError={(e) => {
                    e.target.style.display = 'none'
                    e.target.parentElement.innerHTML = `
                      <div class="w-full h-full flex items-center justify-center">
                        <div class="text-center p-4">
                          <svg class="w-12 h-12 mx-auto mb-2 text-text-tertiary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
                          </svg>
                          <p class="text-sm text-text-secondary">Không thể tải ảnh</p>
                        </div>
                      </div>
                    `
                  }}
                />
              ) : (
                <div className="w-full h-full flex items-center justify-center">
                  <div className="text-center p-4">
                    <ImageIcon className="w-12 h-12 mx-auto mb-2 text-text-tertiary" />
                    <p className="text-sm text-text-secondary">Chưa có ảnh</p>
                  </div>
                </div>
              )}
            </div>
          </Card>
        </div>
      </div>
      
      {/* Confirm Delete Dialog */}
      <ConfirmDialog
        isOpen={isOpen}
        onClose={closeDialog}
        onConfirm={confirmDialog(handleDeleteConfirm)}
        title="Xác nhận xóa nghệ sĩ"
        message={
          artist && (
            <div>
              <p className="mb-2">
                Bạn có chắc chắn muốn xóa nghệ sĩ{' '}
                <span className="font-semibold text-text-primary">&ldquo;{artist.name || artist.artistName}&rdquo;</span>?
              </p>
              <p className="text-sm text-text-tertiary">
                Tất cả bài hát và album liên quan đến nghệ sĩ này cũng sẽ bị ảnh hưởng. Hành động này không thể hoàn tác.
              </p>
            </div>
          )
        }
        confirmText="Xóa nghệ sĩ"
        cancelText="Hủy"
        variant="danger"
        isLoading={isDeleting}
      />
    </div>
  )
}