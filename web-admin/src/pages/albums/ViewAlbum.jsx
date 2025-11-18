import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { motion } from 'framer-motion'
import toast from 'react-hot-toast'
import { useQueryClient } from '@tanstack/react-query'
import { Image as ImageIcon, Edit, ArrowLeft, Trash2 } from 'lucide-react'
import Button from '@components/common/Button'
import Card from '@components/common/Card'
import ConfirmDialog from '@components/common/ConfirmDialog'
import { useConfirmDialog } from '@hooks/useConfirmDialog'
import { getAlbumById, deleteAlbum } from '@services/api/albumService'

// Read-only album view page
export default function ViewAlbum() {
  const { id } = useParams()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { isOpen, openDialog, closeDialog, confirmDialog } = useConfirmDialog()
  const [album, setAlbum] = useState(null)
  const [isLoading, setIsLoading] = useState(true)
  const [isDeleting, setIsDeleting] = useState(false)

  useEffect(() => {
    const load = async () => {
      if (!id) return
      setIsLoading(true)
      try {
        const data = await getAlbumById(id)
        console.log('Fetched album data:', data) // Debug log
        setAlbum(data)
      } catch (error) {
        console.error('Error loading album:', error)
        toast.error('Không thể tải dữ liệu album')
      } finally { 
        setIsLoading(false)
      }
    }
    load()
  }, [id])

  const handleDeleteClick = () => {
    const albumTitle = album?.title || 'album này'
    openDialog({ id, title: albumTitle })
  }

  const handleDeleteConfirm = async () => {
    setIsDeleting(true)
    try {
      await deleteAlbum(id)
      
      // Invalidate React Query cache to force refetch
      queryClient.invalidateQueries({ queryKey: ['albums'] })
      
      toast.success('Xóa album thành công!')
      navigate('/albums')
    } catch (error) {
      console.error('Error deleting album:', error)
      toast.error('Không thể xóa album')
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
          <Card className="p-8 space-y-6"><div className="h-64 bg-bg-secondary rounded animate-pulse" /></Card>
          <Card className="p-8 lg:col-span-2 space-y-6">{[1,2,3,4].map(i => <div key={i} className="h-16 bg-bg-secondary rounded animate-pulse" />)}</Card>
        </div>
      </div>
    )
  }

  // No data found
  if (!album) {
    return (
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <nav className="flex items-center gap-2 text-sm text-text-secondary">
            <button onClick={() => navigate('/albums')} className="p-2 hover:bg-bg-hover rounded-lg transition-colors">
              <ArrowLeft className="w-5 h-5" />
            </button>
            <button onClick={() => navigate('/albums')} className="hover:text-text-primary">Quản lý album</button>
          </nav>
        </div>
        <Card className="p-12 text-center">
          <ImageIcon className="w-16 h-16 mx-auto mb-4 text-text-tertiary" />
          <h3 className="text-xl font-semibold mb-2">Không tìm thấy album</h3>
          <p className="text-text-secondary mb-6">Album này có thể đã bị xóa hoặc không tồn tại</p>
          <Button onClick={() => navigate('/albums')}>Quay lại danh sách</Button>
        </Card>
      </div>
    )
  }

  const formattedDate = album.releaseDate ? (() => { 
    try { 
      const d = new Date(album.releaseDate)
      const y = d.getFullYear()
      const m = String(d.getMonth()+1).padStart(2,'0')
      const day = String(d.getDate()).padStart(2,'0')
      return `${day}/${m}/${y}` 
    } catch { 
      return '-' 
    } 
  })() : '-'

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <nav className="flex items-center gap-2 text-sm text-text-secondary">
          <button onClick={() => navigate('/albums')} className="p-2 hover:bg-bg-hover rounded-lg transition-colors"><ArrowLeft className="w-5 h-5" /></button>
          <button onClick={() => navigate('/albums')} className="hover:text-text-primary">Quản lý album</button><span>/</span><span className="text-text-primary">Chi tiết album</span>
        </nav>
        <div className="flex gap-3">
          <Button variant="secondary" icon={Edit} onClick={() => navigate(`/albums/${id}/edit`)}>Chỉnh sửa</Button>
          <Button variant="danger" icon={Trash2} onClick={handleDeleteClick}>Xóa</Button>
        </div>
      </div>
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <motion.div initial={{ opacity:0, x:-20 }} animate={{ opacity:1, x:0 }}>
          <Card className="p-8">
            <h2 className="text-xl font-semibold text-text-primary mb-6">Ảnh bìa</h2>
            {album.coverImageUrl ? (
              <div className="space-y-4">
                <div className="w-full aspect-square rounded-lg overflow-hidden"><img src={album.coverImageUrl} alt="Cover" className="w-full h-full object-cover" /></div>
              </div>
            ) : (
              <div className="border-2 border-dashed border-border rounded-lg p-12 text-center">
                <ImageIcon className="w-12 h-12 mx-auto mb-4 text-text-tertiary" />
                <p className="text-text-tertiary">Không có ảnh</p>
              </div>
            )}
          </Card>
        </motion.div>
        <motion.div initial={{ opacity:0, x:20 }} animate={{ opacity:1, x:0 }} className="lg:col-span-2">
          <Card className="p-8">
            <h2 className="text-xl font-semibold text-text-primary mb-6">Thông tin album</h2>
            <div className="space-y-6">
              <div>
                <label className="block text-sm font-medium text-text-primary mb-2">Tên album</label>
                <div className="px-4 py-3 bg-bg-secondary rounded-lg text-text-primary border border-border">{album.title || '-'}</div>
              </div>
              <div>
                <label className="block text-sm font-medium text-text-primary mb-2">Nghệ sĩ</label>
                <div className="px-4 py-3 bg-bg-secondary rounded-lg text-text-primary border border-border">{album.artistName || '-'}</div>
              </div>
              <div>
                <label className="block text-sm font-medium text-text-primary mb-2">Ngày phát hành</label>
                <div className="px-4 py-3 bg-bg-secondary rounded-lg text-text-primary border border-border">{formattedDate}</div>
              </div>
              <div>
                <label className="block text-sm font-medium text-text-primary mb-2">Mô tả</label>
                <div className="px-4 py-3 bg-bg-secondary rounded-lg text-text-primary border border-border min-h-[100px]">{album.description || '-'}</div>
              </div>
            </div>
          </Card>
        </motion.div>
      </div>
      
      {/* Confirm Delete Dialog */}
      <ConfirmDialog
        isOpen={isOpen}
        onClose={closeDialog}
        onConfirm={confirmDialog(handleDeleteConfirm)}
        title="Xác nhận xóa album"
        message={
          album && (
            <div>
              <p className="mb-2">
                Bạn có chắc chắn muốn xóa album{' '}
                <span className="font-semibold text-text-primary">&ldquo;{album.title}&rdquo;</span>?
              </p>
              <p className="text-sm text-text-tertiary">
                Tất cả bài hát trong album này sẽ mất thông tin album. Hành động này không thể hoàn tác.
              </p>
            </div>
          )
        }
        confirmText="Xóa album"
        cancelText="Hủy"
        variant="danger"
        isLoading={isDeleting}
      />
    </div>
  )
}
