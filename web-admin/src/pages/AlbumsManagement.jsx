import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { Plus, Grid, List, Search, MoreVertical, Edit, Trash2, Disc } from 'lucide-react'
import Button from '@components/common/Button'
import Card from '@components/common/Card'
import ConfirmDialog from '@components/common/ConfirmDialog'
import { useConfirmDialog } from '@hooks/useConfirmDialog'
import { TableSkeleton } from '@components/common/LoadingScreen'
import { getAllAlbums, deleteAlbum } from '@services/api/albumService'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { formatDate } from '../lib/formatters'
import toast from 'react-hot-toast'

export default function AlbumsManagement() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { isOpen, data: selectedAlbum, openDialog, closeDialog, confirmDialog } = useConfirmDialog()
  const [viewMode, setViewMode] = useState('grid') // 'grid' or 'table'
  const [searchQuery, setSearchQuery] = useState('')
  const [page, setPage] = useState(1)
  const [itemsPerPage] = useState(20)
  const [isDeleting, setIsDeleting] = useState(false)

  // Refetch data when component mounts
  useEffect(() => {
    refetch()
  })

  // Fetch albums from API
  const { data: albumsData, isLoading, refetch } = useQuery({
    queryKey: ['albums'],
    queryFn: getAllAlbums,
    staleTime: 0, // Always refetch on component mount
    cacheTime: 5 * 60 * 1000, // Keep in cache for 5 minutes
    refetchOnMount: 'always', // Always refetch when component mounts
    refetchOnWindowFocus: true, // Refetch when window regains focus
  })

  // Filter and paginate
  const filteredAlbums = albumsData?.filter(album =>
    album.title?.toLowerCase().includes(searchQuery.toLowerCase()) ||
    album.artistName?.toLowerCase().includes(searchQuery.toLowerCase())
  ) || []

  const totalAlbums = filteredAlbums.length
  const totalPages = Math.ceil(totalAlbums / itemsPerPage)
  const startIndex = (page - 1) * itemsPerPage
  const endIndex = startIndex + itemsPerPage
  const paginatedAlbums = filteredAlbums.slice(startIndex, endIndex)

  const handleDeleteClick = (album) => {
    openDialog({ id: album.albumId, title: album.title })
  }

  const handleDeleteConfirm = async (albumData) => {
    setIsDeleting(true)
    try {
      await deleteAlbum(albumData.id)
      
      // Invalidate and refetch albums list
      queryClient.invalidateQueries({ queryKey: ['albums'] })
      await refetch()
      
      toast.success(`Đã xóa album "${albumData.title}"!`)
    } catch (error) {
      console.error('Error deleting album:', error)
      toast.error('Không thể xóa album')
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
            Quản lý Album
          </h1>
          <p className="text-text-secondary">
            Tổng cộng {totalAlbums} album
          </p>
        </div>
        <Button
          icon={Plus}
          iconPosition="left"
          onClick={() => navigate('/albums/add')}
        >
          Thêm album
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
              placeholder="Tìm kiếm album..."
              value={searchQuery}
              onChange={(e) => {
                setSearchQuery(e.target.value)
                setPage(1)
              }}
              className="w-full pl-10 pr-4 py-2 bg-bg-secondary border border-border rounded-lg text-text-primary placeholder:text-text-tertiary focus:outline-none focus:border-spotify-green"
            />
          </div>

          {/* View Toggle */}
          <div className="flex items-center gap-2">
            <button
              onClick={() => setViewMode('grid')}
              className={`p-2 rounded-lg transition-colors ${
                viewMode === 'grid'
                  ? 'bg-spotify-green text-white'
                  : 'bg-bg-secondary text-text-tertiary hover:text-text-primary'
              }`}
            >
              <Grid className="w-5 h-5" />
            </button>
            <button
              onClick={() => setViewMode('table')}
              className={`p-2 rounded-lg transition-colors ${
                viewMode === 'table'
                  ? 'bg-spotify-green text-white'
                  : 'bg-bg-secondary text-text-tertiary hover:text-text-primary'
              }`}
            >
              <List className="w-5 h-5" />
            </button>
          </div>
        </div>
      </Card>

      {/* Content */}
      {isLoading ? (
        <Card className="p-6">
          <TableSkeleton rows={10} columns={5} />
        </Card>
      ) : viewMode === 'grid' ? (
        /* Grid View */
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {paginatedAlbums.map((album) => (
            <motion.div
              key={album.albumId}
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              whileHover={{ y: -4 }}
              transition={{ duration: 0.2 }}
            >
              <Card className="p-6 cursor-pointer group" onClick={() => navigate(`/albums/${album.albumId}`)}>
                {/* Album Cover */}
                <div className="relative mb-4">
                  <div className="w-full aspect-square rounded-lg overflow-hidden bg-bg-secondary flex items-center justify-center">
                    {album.coverImageUrl ? (
                      <img
                        src={album.coverImageUrl}
                        alt={album.title}
                        className="w-full h-full object-cover"
                        // onError={(e) => {
                        // //   e.target.src = '/placeholder-album.jpg'
                        // }}
                      />
                    ) : (
                      <Disc className="w-16 h-16 text-text-tertiary" />
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
                            onClick={() => navigate(`/albums/${album.albumId}/edit`)}
                            className="w-full flex items-center gap-3 px-3 py-2 hover:bg-bg-hover rounded text-text-secondary hover:text-text-primary text-sm transition-colors"
                          >
                            <Edit className="w-4 h-4" />
                            Chỉnh sửa
                          </button>
                          <button
                            onClick={() => handleDeleteClick(album)}
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

                {/* Album Info */}
                <div>
                  <h3 className="text-lg font-semibold text-text-primary mb-1 truncate">
                    {album.title}
                  </h3>
                  <p className="text-sm text-text-tertiary mb-2">
                    {album.artistName || 'Unknown Artist'}
                  </p>
                  <div className="flex items-center justify-between text-xs text-text-tertiary">
                    <span>{album.totalSongs || 0} bài hát</span>
                    {album.releaseDate && (
                      <span>{new Date(album.releaseDate).getFullYear()}</span>
                    )}
                  </div>
                </div>
              </Card>
            </motion.div>
          ))}
        </div>
      ) : (
        /* Table View */
        <Card className="p-0 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-bg-secondary">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-text-tertiary uppercase">
                    Album
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-text-tertiary uppercase">
                    Nghệ sĩ
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-text-tertiary uppercase">
                    Bài hát
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-text-tertiary uppercase">
                    Ngày phát hành
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-text-tertiary uppercase">
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                {paginatedAlbums.map((album) => (
                  <tr
                    key={album.albumId}
                    onClick={() => navigate(`/albums/${album.albumId}`)}
                    className="hover:bg-bg-hover transition-colors cursor-pointer"
                  >
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-3">
                        <div className="w-12 h-12 rounded overflow-hidden bg-bg-secondary flex items-center justify-center flex-shrink-0">
                          {album.coverImageUrl ? (
                            <img
                              src={album.coverImageUrl}
                              alt={album.title}
                              className="w-full h-full object-cover"
                            //   onError={(e) => {
                            //     e.target.src = '/placeholder-album.jpg'
                            //   }}
                            />
                          ) : (
                            <Disc className="w-6 h-6 text-text-tertiary" />
                          )}
                        </div>
                        <span className="font-medium text-text-primary">
                          {album.title}
                        </span>
                      </div>
                    </td>
                    <td className="px-6 py-4 text-text-secondary">
                      {album.artistName || '-'}
                    </td>
                    <td className="px-6 py-4 text-text-secondary">
                      {album.totalSongs || 0}
                    </td>
                    <td className="px-6 py-4 text-text-secondary">
                      {album.releaseDate ? formatDate(album.releaseDate) : '-'}
                    </td>
                    <td className="px-6 py-4">
                      <div className="relative group/action" onClick={(e) => e.stopPropagation()}>
                        <button className="p-1.5 hover:bg-bg-hover rounded transition-colors">
                          <MoreVertical className="w-4 h-4 text-text-tertiary" />
                        </button>
                        
                        <div className="absolute right-0 top-full mt-1 w-48 bg-bg-card border border-border rounded-lg shadow-xl opacity-0 invisible group-hover/action:opacity-100 group-hover/action:visible transition-all z-20">
                          <div className="p-1">
                            <button
                              onClick={() => navigate(`/albums/${album.albumId}/edit`)}
                              className="w-full flex items-center gap-3 px-3 py-2 hover:bg-bg-hover rounded text-text-secondary hover:text-text-primary text-sm transition-colors"
                            >
                              <Edit className="w-4 h-4" />
                              Chỉnh sửa
                            </button>
                            <button
                              onClick={() => handleDeleteClick(album)}
                              className="w-full flex items-center gap-3 px-3 py-2 hover:bg-bg-hover rounded text-red-500 text-sm transition-colors"
                            >
                              <Trash2 className="w-4 h-4" />
                              Xóa
                            </button>
                          </div>
                        </div>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Pagination */}
          <div className="px-6 py-4 border-t border-border flex items-center justify-between">
            <p className="text-sm text-text-secondary">
              Hiển thị {startIndex + 1} đến {Math.min(endIndex, totalAlbums)} trong tổng số {totalAlbums} album
            </p>
            <div className="flex items-center gap-2">
              <Button
                variant="secondary"
                size="sm"
                disabled={page === 1}
                onClick={() => setPage(page - 1)}
              >
                Trước
              </Button>
              <span className="text-sm text-text-secondary px-3">
                Trang {page} / {totalPages}
              </span>
              <Button
                variant="secondary"
                size="sm"
                disabled={page >= totalPages}
                onClick={() => setPage(page + 1)}
              >
                Sau
              </Button>
            </div>
          </div>
        </Card>
      )}

      {/* Empty State */}
      {!isLoading && paginatedAlbums.length === 0 && (
        <Card className="p-12 text-center">
          <Disc className="w-16 h-16 mx-auto mb-4 text-text-tertiary" />
          <h3 className="text-xl font-semibold text-text-primary mb-2">
            Không tìm thấy album
          </h3>
          <p className="text-text-secondary mb-6">
            {searchQuery
              ? 'Thử tìm kiếm với từ khóa khác'
              : 'Bắt đầu bằng cách thêm album mới'}
          </p>
          {!searchQuery && (
            <Button icon={Plus} onClick={() => navigate('/albums/add')}>
              Thêm album đầu tiên
            </Button>
          )}
        </Card>
      )}
      
      {/* Confirm Delete Dialog */}
      <ConfirmDialog
        isOpen={isOpen}
        onClose={closeDialog}
        onConfirm={confirmDialog(handleDeleteConfirm)}
        title="Xác nhận xóa album"
        message={
          selectedAlbum && (
            <div>
              <p className="mb-2">
                Bạn có chắc chắn muốn xóa album{' '}
                <span className="font-semibold text-text-primary">&ldquo;{selectedAlbum.title}&rdquo;</span>?
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
