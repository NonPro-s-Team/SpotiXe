import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { motion } from 'framer-motion'
import toast from 'react-hot-toast'
import { useQueryClient } from '@tanstack/react-query'
import { Save, X, ArrowLeft } from 'lucide-react'
import Button from '@components/common/Button'
import Card from '@components/common/Card'
import Input from '@components/common/Input'
import { createArtist } from '@services/api/artistService'

// Standalone add artist page (split from unified ArtistForm)
export default function AddArtist() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [imagePreview, setImagePreview] = useState(null)
  const [isLoading, setIsLoading] = useState(false)
  const { register, handleSubmit, formState: { errors } } = useForm({
    mode: 'onSubmit', // Only validate on submit
    defaultValues: { artistName: '', bio: '', country: '', profileImageUrl: '' }
  })
  
  // Debug: watch form values
  // const formValues = watch()
  // console.log('Form values:', formValues)
  // console.log('Form errors:', errors)
  
  const onSubmit = async (data) => {
    setIsLoading(true)
    try {
      const payload = {
        name: data.artistName, // Backend expects 'name' field
        bio: data.bio,
        country: data.country,
        genre: data.genre,
        profileImageUrl: data.profileImageUrl,
      }
      console.log('Submitting artist data:', payload)
      await createArtist(payload)
      
      // Invalidate React Query cache to force refetch
      queryClient.invalidateQueries({ queryKey: ['artists'] })
      
      toast.success('Thêm nghệ sĩ thành công!')
      navigate('/artists')
    } catch (err) {
      console.error('Error creating artist:', err)
      toast.error(err.message || 'Không thể thêm nghệ sĩ')
    } finally { setIsLoading(false) }
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <nav className="flex items-center gap-2 text-sm text-text-secondary">
          <button onClick={() => navigate('/artists')} className="p-2 hover:bg-bg-hover rounded-lg transition-colors"><ArrowLeft className="w-5 h-5" /></button>
          <button onClick={() => navigate('/artists')} className="hover:text-text-primary">Quản lý nghệ sĩ</button>
          <span>/</span><span className="text-text-primary">Thêm nghệ sĩ mới</span>
        </nav>
        <div />
      </div>
      <form onSubmit={handleSubmit(onSubmit)}>
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Left - Image */}
          <motion.div initial={{ opacity:0, x:-20 }} animate={{ opacity:1, x:0 }}>
            <Card className="p-8">
  <h2 className="text-xl font-semibold text-text-primary mb-6">Ảnh đại diện</h2>

  {/* Input nhập URL ảnh */}
  <div className="space-y-4">
    <div>
      <label className="block text-sm font-medium text-text-primary mb-2">
        URL ảnh đại diện <span className="text-red-500">*</span>
      </label>
      <input
        type="url"
        placeholder="https://cdn.spotixe.io/artists/den-vau.jpg"
        {...register('profileImageUrl', { 
          onChange: (e) => setImagePreview(e.target.value)
        })}
        className="w-full px-4 py-2 bg-bg-secondary border border-border rounded-lg text-text-primary placeholder:text-text-tertiary focus:outline-none focus:border-spotify-green"
      />
      {errors.profileImageUrl && (
        <p className="text-sm text-red-500 mt-1">{errors.profileImageUrl.message}</p>
      )}
    </div>

    {/* Hiển thị preview ảnh nếu có URL */}
    {imagePreview && (
      <div className="mt-4">
        <div className="w-full aspect-square rounded-full overflow-hidden border border-border">
          <img
            src={imagePreview}
            alt="Preview"
            className="w-full h-full object-cover"
            onError={(e) => {
              e.currentTarget.src =
                'https://via.placeholder.com/300x300.png?text=Invalid+Image+URL';
            }}
          />
        </div>
      </div>
    )}
  </div>
</Card>

          </motion.div>
          {/* Right - Form */}
          <motion.div initial={{ opacity:0, x:20 }} animate={{ opacity:1, x:0 }} className="lg:col-span-2">
            <Card className="p-8">
              <h2 className="text-xl font-semibold text-text-primary mb-6">Thông tin nghệ sĩ</h2>
              <div className="space-y-6">
                <div>
                  <label className="block text-sm font-medium text-text-primary mb-2">Tên nghệ sĩ <span className="text-red-500">*</span></label>
                  <Input {...register('artistName', { required: 'Tên nghệ sĩ là bắt buộc' })} placeholder="Nhập tên nghệ sĩ" error={errors.artistName?.message} />
                </div>
                <div>
                  <label className="block text-sm font-medium text-text-primary mb-2">Tiểu sử</label>
                  <textarea {...register('bio')} placeholder="Mô tả về nghệ sĩ..." rows={4} className="w-full px-4 py-3 bg-bg-secondary border border-border rounded-lg text-text-primary placeholder:text-text-tertiary focus:outline-none focus:border-spotify-green resize-none" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-text-primary mb-2">Quốc gia</label>
                  <Input {...register('country')} placeholder="VD: Việt Nam" />
                </div>
              </div>
            </Card>
          </motion.div>
        </div>
        <motion.div initial={{ opacity:0, y:20 }} animate={{ opacity:1, y:0 }} className="flex items-center justify-between mt-6">
          <div />
          <div className="flex gap-3">
            <Button type="button" variant="secondary" icon={X} onClick={() => navigate('/artists')}>Hủy</Button>
            <Button type="submit" icon={Save} disabled={isLoading}>{isLoading ? 'Đang lưu...' : 'Thêm nghệ sĩ'}</Button>
          </div>
        </motion.div>
      </form>
    </div>
  )
}
