import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { Plus, Grid, List, Search, MoreVertical, Edit, Trash2, User } from 'lucide-react'
import Button from '@components/common/Button'
import Card from '@components/common/Card'
import ConfirmDialog from '@components/common/ConfirmDialog'
import { useConfirmDialog } from '@hooks/useConfirmDialog'
import { TableSkeleton } from '@components/common/LoadingScreen'
import { getAllArtists, deleteArtist } from '@services/api/artistService'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import toast from 'react-hot-toast'

export default function ArtistsManagement() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { isOpen, data: selectedArtist, openDialog, closeDialog, confirmDialog } = useConfirmDialog()
  const [viewMode, setViewMode] = useState('grid')
  const [searchQuery, setSearchQuery] = useState('')
  const [page, setPage] = useState(1)
  const [itemsPerPage] = useState(100)
  const [isDeleting, setIsDeleting] = useState(false)

  // Refetch data when component mounts (e.g., when coming back from edit page)
  useEffect(() => {
    refetch()
  },)

  // Fetch artists from API
  const { data: artistsData, isLoading, refetch } = useQuery({
    queryKey: ['artists'],
    queryFn: getAllArtists,
    staleTime: 0, // Always refetch on component mount
    cacheTime: 5 * 60 * 1000, // Keep in cache for 5 minutes
    refetchOnMount: 'always', // Always refetch when component mounts
    refetchOnWindowFocus: true, // Refetch when window regains focus
  })

  // Filter and paginate
  const filteredArtists = artistsData?.filter(artist =>
    artist.name?.toLowerCase().includes(searchQuery.toLowerCase())
  ) || []

  const totalArtists = filteredArtists.length
  const totalPages = Math.ceil(totalArtists / itemsPerPage)
  const startIndex = (page - 1) * itemsPerPage
  const endIndex = startIndex + itemsPerPage
  const paginatedArtists = filteredArtists.slice(startIndex, endIndex)

  const handleDeleteClick = (artist) => {
    openDialog({ id: artist.artistId, name: artist.name })
  }

  const handleDeleteConfirm = async (artistData) => {
    setIsDeleting(true)
    try {
      await deleteArtist(artistData.id)
      
      // Invalidate and refetch artists list
      queryClient.invalidateQueries({ queryKey: ['artists'] })
      await refetch()
      
      toast.success(`Đã xóa nghệ sĩ "${artistData.name}"!`)
    } catch (error) {
      console.error('Error deleting artist:', error)
      toast.error('Không thể xóa nghệ sĩ')
    } finally {
      setIsDeleting(false)
    }
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-text-primary mb-2">
            Quản lý Nghệ sĩ
          </h1>
          <p className="text-text-secondary">
            Tổng cộng {totalArtists} nghệ sĩ
          </p>
        </div>
        <Button
          icon={Plus}
          iconPosition="left"
          onClick={() => navigate('/artists/add')}
        >
          Thêm nghệ sĩ
        </Button>
      </div>

      {/* Controls */}
      <Card className="p-4">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          {/* Search */}
          <div className="relative flex-1 max-w-md">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-text-tertiary" />
            <input
              type="text"
              placeholder="Tìm kiếm nghệ sĩ..."
              value={searchQuery}
              onChange={(e) => {
                setSearchQuery(e.target.value)
                setPage(1)
              }}
              className="w-full pl-10 pr-4 py-2 bg-bg-secondary border border-border rounded-lg text-text-primary placeholder:text-text-tertiary focus:outline-none focus:border-spotify-green"
            />
          </div>
        </div>
      </Card>

      {/* Content */}
      {isLoading ? (
        <Card className="p-6">
          <TableSkeleton rows={10} columns={4} />
        </Card>
      ) : viewMode === 'grid' ? (
        /* Grid View */
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {paginatedArtists.map((artist) => (
            <motion.div
              key={artist.artistId}
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              whileHover={{ y: -4 }}
              transition={{ duration: 0.2 }}
            >
              <Card className="p-6 cursor-pointer group" onClick={() => navigate(`/artists/${artist.artistId}`)}>
                {/* Artist Image */}
                <div className="relative mb-4">
                  <div className="w-full aspect-square rounded-full overflow-hidden bg-bg-secondary flex items-center justify-center">
                    {artist.profileImageUrl ? (
                      <img
                        src={artist.profileImageUrl}
                        alt={artist.name}
                        className="w-full h-full object-cover"
                        onError={(e) => {
                          e.target.src = '/placeholder-artist.jpg'
                        }}
                      />
                    ) : (
                      <User className="w-16 h-16 text-text-tertiary" />
                    )}
                  </div>
                  
                  {/* Action Menu */}
                  <div className="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition-opacity">
                    <div className="relative group/action" onClick={(e) => e.stopPropagation()}>
                      <button className="p-2 bg-bg-card rounded-full hover:bg-bg-hover transition-colors">
                        <MoreVertical className="w-4 h-4 text-text-primary" />
                      </button>
                      
                      <div className="absolute right-0 top-full mt-1 w-48 bg-bg-card border border-border rounded-lg shadow-xl opacity-0 invisible group-hover/action:opacity-100 group-hover/action:visible transition-all z-20">
                        <div className="p-1">
                          <button
                            onClick={() => navigate(`/artists/${artist.artistId}/edit`)}
                            className="w-full flex items-center gap-3 px-3 py-2 hover:bg-bg-hover rounded text-text-secondary hover:text-text-primary text-sm transition-colors"
                          >
                            <Edit className="w-4 h-4" />
                            Chỉnh sửa
                          </button>
                          <button
                            onClick={() => handleDeleteClick(artist)}
                            className="w-full flex items-center gap-3 px-3 py-2 hover:bg-bg-hover rounded text-red-500 text-sm transition-colors"
                          >
                            <Trash2 className="w-4 h-4" />
                            Xóa
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                {/* Artist Info */}
                <div className="text-center">
                  <h3 className="text-lg font-semibold text-text-primary mb-1 truncate">
                    {artist.name}
                  </h3>
                  <p className="text-sm text-text-tertiary">
                    {artist.bio || 'Chưa có mô tả'}
                  </p>
                  {/* <div className="mt-3 flex items-center justify-center gap-4 text-xs text-text-tertiary">
                    <span>{artist.totalSongs || 0} bài hát</span>
                    <span>{artist.totalAlbums || 0} album</span>
                  </div> */}
                </div>
              </Card>
            </motion.div>
          ))}
        </div>
      ) : (
        <div className="text-center">
          <p className="text-text-secondary">Không có nghệ sĩ nào để hiển thị</p>
        </div>
      )}

      {/* Empty State */}
      {!isLoading && paginatedArtists.length === 0 && (
        <Card className="p-12 text-center">
          <User className="w-16 h-16 mx-auto mb-4 text-text-tertiary" />
          <h3 className="text-xl font-semibold text-text-primary mb-2">
            Không tìm thấy nghệ sĩ
          </h3>
          <p className="text-text-secondary mb-6">
            {searchQuery
              ? 'Thử tìm kiếm với từ khóa khác'
              : 'Bắt đầu bằng cách thêm nghệ sĩ mới'}
          </p>
          {!searchQuery && (
            <Button icon={Plus} onClick={() => navigate('/artists/add')}>
              Thêm nghệ sĩ đầu tiên
            </Button>
          )}
        </Card>
      )}
      
      {/* Confirm Delete Dialog */}
      <ConfirmDialog
        isOpen={isOpen}
        onClose={closeDialog}
        onConfirm={confirmDialog(handleDeleteConfirm)}
        title="Xác nhận xóa nghệ sĩ"
        message={
          selectedArtist && (
            <div>
              <p className="mb-2">
                Bạn có chắc chắn muốn xóa nghệ sĩ{' '}
                <span className="font-semibold text-text-primary">&ldquo;{selectedArtist.name}&rdquo;</span>?
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
