import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { motion } from 'framer-motion'
import toast from 'react-hot-toast'
import { useQueryClient } from '@tanstack/react-query'
import { Image as ImageIcon, Save, X, ArrowLeft, Calendar } from 'lucide-react'
import Button from '@components/common/Button'
import Card from '@components/common/Card'
import Input from '@components/common/Input'
import { createAlbum } from '@services/api/albumService'
import { getAllArtists } from '@services/api/artistService'
import { useQuery } from '@tanstack/react-query'

// Standalone add album page (split from unified AlbumForm)
export default function AddAlbum() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [imagePreview, setImagePreview] = useState(null)
  const [artistSearch, setArtistSearch] = useState('')
  const [showArtistDropdown, setShowArtistDropdown] = useState(false)
  // track selected by id only in add mode; name not needed
  const { data: artistsData } = useQuery({ queryKey: ['artists'], queryFn: getAllArtists })
  const artists = [
    { value: 'create-new', label: '+ Tạo nghệ sĩ mới', isSpecial: true },
    ...(artistsData?.map(a => ({ value: a.artistId?.toString?.() || String(a.artistId), label: a.name })) || [])
  ]
  const filteredArtists = artists.filter(a => a.label.toLowerCase().includes(artistSearch.toLowerCase()))

  const { register, handleSubmit, setValue, formState: { errors } } = useForm({
    defaultValues: { title: '', artistId: '', releaseDate: '', coverImageUrl: '', description: '' }
  })

  const onSubmit = async (data) => {
    try {
      await createAlbum({
        title: data.title,
        artistId: parseInt(data.artistId),
        releaseDate: data.releaseDate || null,
        coverImageUrl: data.coverImageUrl || null,
        description: data.description || null,
      })
      
      // Invalidate React Query cache to force refetch
      queryClient.invalidateQueries({ queryKey: ['albums'] })
      
      toast.success('Thêm album thành công!')
      navigate('/albums')
    } catch (error) {
      console.error('Error creating album:', error)
      toast.error(error.message || 'Không thể thêm album')
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <nav className="flex items-center gap-2 text-sm text-text-secondary">
          <button onClick={() => navigate('/albums')} className="p-2 hover:bg-bg-hover rounded-lg transition-colors"><ArrowLeft className="w-5 h-5" /></button>
          <button onClick={() => navigate('/albums')} className="hover:text-text-primary">Quản lý album</button><span>/</span><span className="text-text-primary">Thêm album mới</span>
        </nav>
      </div>
      <form onSubmit={handleSubmit(onSubmit)}>
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <motion.div initial={{ opacity:0, x:-20 }} animate={{ opacity:1, x:0 }}>
            <Card className="p-8">
              <h2 className="text-xl font-semibold text-text-primary mb-6">Ảnh bìa</h2>
              
              {/* Input nhập URL ảnh */}
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-text-primary mb-2">
                    URL ảnh bìa
                  </label>
                  <input
                    type="url"
                    placeholder="https://cdn.spotixe.io/albums/album-cover.jpg"
                    {...register('coverImageUrl', {
                      onChange: (e) => setImagePreview(e.target.value)
                    })}
                    className="w-full px-4 py-2 bg-bg-secondary border border-border rounded-lg text-text-primary placeholder:text-text-tertiary focus:outline-none focus:border-spotify-green"
                  />
                  {errors.coverImageUrl && (
                    <p className="text-sm text-red-500 mt-1">{errors.coverImageUrl.message}</p>
                  )}
                </div>

                {/* Hiển thị preview ảnh nếu có URL */}
                {imagePreview && (
                  <div className="mt-4">
                    <p className="text-sm font-medium text-text-primary mb-2">Preview:</p>
                    <div className="w-full aspect-square rounded-lg overflow-hidden border border-border">
                      <img
                        src={imagePreview}
                        alt="Preview"
                        className="w-full h-full object-cover"
                        onError={(e) => {
                          e.currentTarget.src = 'https://via.placeholder.com/400x400.png?text=Invalid+Image+URL';
                        }}
                      />
                    </div>
                  </div>
                )}

                {/* Placeholder nếu chưa có ảnh */}
                {!imagePreview && (
                  <div className="border-2 border-dashed border-border rounded-lg p-12 text-center">
                    <ImageIcon className="w-12 h-12 mx-auto mb-4 text-text-tertiary" />
                    <p className="text-text-primary font-medium mb-2">Nhập URL ảnh bìa</p>
                    <p className="text-sm text-text-tertiary">Ảnh sẽ hiển thị preview ở đây</p>
                  </div>
                )}
              </div>
            </Card>
          </motion.div>
          <motion.div initial={{ opacity:0, x:20 }} animate={{ opacity:1, x:0 }} className="lg:col-span-2">
            <Card className="p-8">
              <h2 className="text-xl font-semibold text-text-primary mb-6">Thông tin album</h2>
              <div className="space-y-6">
                <div>
                  <label className="block text-sm font-medium text-text-primary mb-2">Tên album <span className="text-red-500">*</span></label>
                  <Input {...register('title', { required: 'Tên album là bắt buộc' })} placeholder="Nhập tên album" error={errors.title?.message} />
                </div>
                <div className="relative">
                  <label className="block text-sm font-medium text-text-primary mb-2">Nghệ sĩ <span className="text-red-500">*</span></label>
                  <Input type="text" value={artistSearch} onChange={(e) => { setArtistSearch(e.target.value); setShowArtistDropdown(true) }} onFocus={() => setShowArtistDropdown(true)} placeholder="Tìm kiếm hoặc nhập tên nghệ sĩ..." error={errors.artistId?.message} />
                  <input type="hidden" {...register('artistId', { required: 'Nghệ sĩ là bắt buộc' })} />
                  {showArtistDropdown && (
                    <>
                      <div className="fixed inset-0 z-10" onClick={() => setShowArtistDropdown(false)} />
                      <div className="absolute z-20 w-full mt-1 bg-bg-card border border-border rounded-lg shadow-xl max-h-60 overflow-y-auto">
                        {filteredArtists.map((artist) => (
                          <button key={artist.value} type="button" onClick={() => {
                            if (artist.value === 'create-new') { navigate('/artists/add') } else { setValue('artistId', artist.value); setArtistSearch(artist.label); setShowArtistDropdown(false) }
                          }} className={`w-full px-4 py-2 text-left hover:bg-bg-hover transition-colors ${artist.isSpecial ? 'text-spotify-green font-medium border-b border-border' : 'text-text-primary'}`}>
                            {artist.label}
                          </button>
                        ))}
                      </div>
                    </>
                  )}
                </div>
                <div>
                  <label className="block text-sm font-medium text-text-primary mb-2">Ngày phát hành</label>
                  <div className="relative">
                    <Input type="date" {...register('releaseDate')} />
                    <Calendar className="absolute right-3 top-1/2 -translate-y-1/2 w-5 h-5 text-text-tertiary pointer-events-none" />
                  </div>
                </div>
                <div>
                  <label className="block text-sm font-medium text-text-primary mb-2">Mô tả</label>
                  <textarea {...register('description')} placeholder="Mô tả về album..." rows={4} className="w-full px-4 py-3 bg-bg-secondary border border-border rounded-lg text-text-primary placeholder:text-text-tertiary focus:outline-none focus:border-spotify-green resize-none" />
                </div>
              </div>
            </Card>
          </motion.div>
        </div>
        <motion.div initial={{ opacity:0, y:20 }} animate={{ opacity:1, y:0 }} className="flex items-center justify-between mt-6">
          <div />
          <div className="flex gap-3">
            <Button type="button" variant="secondary" icon={X} onClick={() => navigate('/albums')}>Hủy</Button>
            <Button type="submit" icon={Save}>Thêm album</Button>
          </div>
        </motion.div>
      </form>
    </div>
  )
}
