import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { motion } from 'framer-motion'
import toast from 'react-hot-toast'
import { useQueryClient } from '@tanstack/react-query'
import { Image as ImageIcon, Save, X, ArrowLeft, Trash2 } from 'lucide-react'
import Button from '@components/common/Button'
import Card from '@components/common/Card'
import Input from '@components/common/Input'
import ConfirmDialog from '@components/common/ConfirmDialog'
import { useConfirmDialog } from '@hooks/useConfirmDialog'
import { getArtistById, updateArtist, deleteArtist } from '@services/api/artistService'

// Standalone edit artist page
export default function EditArtist() {
  const { id } = useParams()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { isOpen, openDialog, closeDialog, confirmDialog } = useConfirmDialog()
  const [imagePreview, setImagePreview] = useState(null)
  const [isLoading, setIsLoading] = useState(false)
  const [isFetching, setIsFetching] = useState(true) // Thêm loading state riêng cho fetch
  const [isDeleting, setIsDeleting] = useState(false)
  const [artistName, setArtistName] = useState('')
  
  const { register, handleSubmit, setValue, formState: { errors } } = useForm({
    defaultValues: { artistName: '', bio: '', country: '', genre: '', debutYear: '', profileImageUrl: '' }
  })

  useEffect(() => {
    const load = async () => {
      if (!id) return
      setIsFetching(true)
      try {
        const artist = await getArtistById(id)
        console.log('Fetched artist data:', artist) // Debug log
        
        // Store artist name for delete confirmation
        setArtistName(artist.name || artist.artistName || '')
        
        // Map API fields to form fields (handle both 'name' and 'artistName')
        setValue('artistName', artist.name || artist.artistName || '')
        setValue('bio', artist.bio || '')
        setValue('country', artist.country || '')
        setValue('genre', artist.genre || '')
        setValue('debutYear', artist.debutYear?.toString() || '')
        
        if (artist.profileImageUrl) {
          setImagePreview(artist.profileImageUrl)
          setValue('profileImageUrl', artist.profileImageUrl)
        }
      } catch (error) {
        console.error('Error loading artist:', error)
        toast.error('Không thể tải dữ liệu nghệ sĩ')
        // Optional: redirect back if data cannot be loaded
        // navigate('/artists')
      } finally {
        setIsFetching(false)
      }
    }
    load()
  }, [id, setValue, navigate])

  const handleImageUpload = (e) => {
    const file = e.target.files[0]
    if (file) {
      const reader = new FileReader()
      reader.onloadend = () => {
        setImagePreview(reader.result)
        setValue('profileImageUrl', reader.result)
      }
      reader.readAsDataURL(file)
    }
  }

  const onSubmit = async (data) => {
    setIsLoading(true)
    try {
      // Map form fields to API format
      const payload = {
        name: data.artistName, // Backend expects 'name'
        bio: data.bio,
        country: data.country,
        genre: data.genre,
        debutYear: data.debutYear ? parseInt(data.debutYear) : null,
        profileImageUrl: data.profileImageUrl,
      }
      
      console.log('Updating artist with payload:', payload) // Debug log
      await updateArtist(id, payload)
      
      // Invalidate React Query cache to force refetch
      queryClient.invalidateQueries({ queryKey: ['artists'] })
      
      toast.success('Cập nhật nghệ sĩ thành công!')
      navigate('/artists')
    } catch (error) {
      console.error('Error updating artist:', error)
      toast.error('Không thể cập nhật nghệ sĩ')
    } finally {
      setIsLoading(false)
    }
  }

  const handleDeleteClick = () => {
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

  // Show loading skeleton while fetching data
  if (isFetching) {
    return (
      <div className="space-y-6">
        <div className="h-8 bg-bg-secondary rounded animate-pulse w-64" />
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2 space-y-6">
            <Card className="p-6">
              <div className="space-y-4">
                <div className="h-10 bg-bg-secondary rounded animate-pulse" />
                <div className="h-32 bg-bg-secondary rounded animate-pulse" />
                <div className="grid grid-cols-2 gap-4">
                  <div className="h-10 bg-bg-secondary rounded animate-pulse" />
                  <div className="h-10 bg-bg-secondary rounded animate-pulse" />
                </div>
              </div>
            </Card>
          </div>
          <div className="h-64 bg-bg-secondary rounded animate-pulse" />
        </div>
      </div>
    )
  }

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
          <span className="text-text-primary">Chỉnh sửa nghệ sĩ</span>
        </nav>
        <Button variant="danger" size="sm" onClick={handleDeleteClick} icon={Trash2}>Xóa nghệ sĩ</Button>
      </div>

      <form onSubmit={handleSubmit(onSubmit)}>
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Main form */}
          <div className="lg:col-span-2 space-y-6">
            <Card className="p-6">
              <h2 className="text-xl font-semibold mb-6">Thông tin nghệ sĩ</h2>
              <div className="space-y-4">
                <Input
                  label="Tên nghệ sĩ"
                  {...register('artistName', { required: 'Tên nghệ sĩ là bắt buộc' })}
                  error={errors.artistName?.message}
                  placeholder="Nhập tên nghệ sĩ"
                />
                <Input
                  label="Tiểu sử"
                  {...register('bio')}
                  type="textarea"
                  rows={4}
                  placeholder="Nhập tiểu sử nghệ sĩ"
                />
                <div className="grid grid-cols-2 gap-4">
                  <Input label="Quốc gia" {...register('country')} placeholder="Việt Nam" />
                  <Input label="Thể loại" {...register('genre')} placeholder="Pop, Rock, ..." />
                </div>
                <Input label="Năm ra mắt" {...register('debutYear')} type="number" placeholder="2020" />
              </div>
            </Card>
          </div>

          {/* Image upload */}
          <div className="space-y-6">
            <Card className="p-6">
              <h3 className="text-lg font-semibold mb-4">Ảnh nghệ sĩ</h3>
              <div className="space-y-4">
                <div className="aspect-square rounded-lg border-2 border-dashed border-border-secondary flex items-center justify-center overflow-hidden bg-bg-secondary">
                  {imagePreview ? (
                    <motion.img initial={{ opacity: 0 }} animate={{ opacity: 1 }} src={imagePreview} alt="Preview" className="w-full h-full object-cover" />
                  ) : (
                    <div className="text-center p-4">
                      <ImageIcon className="w-12 h-12 mx-auto mb-2 text-text-tertiary" />
                      <p className="text-sm text-text-secondary">Chưa có ảnh</p>
                    </div>
                  )}
                </div>
                <div className="space-y-2">
                  <label className="block">
                    <input type="file" accept="image/*" onChange={handleImageUpload} className="hidden" />
                    <Button type="button" variant="secondary" size="sm" className="w-full" onClick={() => document.querySelector('input[type="file"]').click()}>Tải ảnh lên</Button>
                  </label>
                  <Input label="URL ảnh" {...register('profileImageUrl')} placeholder="https://example.com/image.jpg" />
                </div>
              </div>
            </Card>

            {/* Action buttons */}
            <div className="flex gap-3">
              <Button type="submit" className="flex-1" icon={Save} loading={isLoading}>Lưu thay đổi</Button>
              <Button type="button" variant="secondary" icon={X} onClick={() => navigate('/artists')}>Hủy</Button>
            </div>
          </div>
        </div>
      </form>
      
      {/* Confirm Delete Dialog */}
      <ConfirmDialog
        isOpen={isOpen}
        onClose={closeDialog}
        onConfirm={confirmDialog(handleDeleteConfirm)}
        title="Xác nhận xóa nghệ sĩ"
        message={
          <div>
            <p className="mb-2">
              Bạn có chắc chắn muốn xóa nghệ sĩ{' '}
              <span className="font-semibold text-text-primary">&ldquo;{artistName}&rdquo;</span>?
            </p>
            <p className="text-sm text-text-tertiary">
              Tất cả bài hát và album liên quan đến nghệ sĩ này cũng sẽ bị ảnh hưởng. Hành động này không thể hoàn tác.
            </p>
          </div>
        }
        confirmText="Xóa nghệ sĩ"
        cancelText="Hủy"
        variant="danger"
        isLoading={isDeleting}
      />
    </div>
  )
}