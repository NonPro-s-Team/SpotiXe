import { useState, useMemo } from 'react'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { Filter, MoreVertical, Edit, Trash2, Plus, Upload, Search } from 'lucide-react'
import { motion } from 'framer-motion'
import Button from '@components/common/Button'
import Card from '@components/common/Card'
import ConfirmDialog from '@components/common/ConfirmDialog'
import { TableSkeleton } from '@components/common/LoadingScreen'
import { Select } from '@components/common/Input'
import { formatDuration } from '@utils/helpers'
import { getAllSongs, deleteSong } from '@services/api/songService'
import { getAllArtists } from '@services/api/artistService'
import { getAllAlbums } from '@services/api/albumService'
import { formatDate, getStatusColor } from '../lib/formatters'
import toast from 'react-hot-toast'

export default function MusicManagement() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [page, setPage] = useState(1)
  const [itemsPerPage, setItemsPerPage] = useState(10) // Mặc định 10 mỗi trang
  const [searchQuery, setSearchQuery] = useState('')
  const [selectedTab, setSelectedTab] = useState('all')
  // const [showUploadModal, setShowUploadModal] = useState(false)
  const [showFilters, setShowFilters] = useState(false)
  const [selectedTracks, setSelectedTracks] = useState([])
  const [filters, setFilters] = useState({
    genre: '',
    status: '',
    dateRange: '',
  })
  
  // Confirm dialog states
  const [showDeleteDialog, setShowDeleteDialog] = useState(false)
  const [itemToDelete, setItemToDelete] = useState(null)
  const [isDeleting, setIsDeleting] = useState(false)
  
  // Dropdown menu states
  const [openDropdown, setOpenDropdown] = useState(null)
  const [dropdownPosition, setDropdownPosition] = useState({ top: 0, right: 0 })
  // Fetch songs from API
  const { data: songsData, isLoading: isLoadingSongs } = useQuery({
    queryKey: ['songs'],
    queryFn: getAllSongs,
    staleTime: 5 * 60 * 1000, // 5 minutes
    cacheTime: 10 * 60 * 1000, // 10 minutes
  })

  // Fetch artists from API
  const { data: artistsData, isLoading: isLoadingArtists } = useQuery({
    queryKey: ['artists'],
    queryFn: getAllArtists,
    staleTime: 5 * 60 * 1000,
    cacheTime: 10 * 60 * 1000,
  })

  // Fetch albums from API
  const { data: albumsData, isLoading: isLoadingAlbums } = useQuery({
    queryKey: ['albums'],
    queryFn: getAllAlbums,
    staleTime: 5 * 60 * 1000,
    cacheTime: 10 * 60 * 1000,
  })

  const isLoading = isLoadingSongs || isLoadingArtists || isLoadingAlbums

  // Create maps for quick lookup
  const artistMap = useMemo(() => {
    const map = new Map()
    artistsData?.forEach((artist) => {
      map.set(artist.artistId, artist.name)
    })
    return map
  }, [artistsData])

  const albumMap = useMemo(() => {
    const map = new Map()
    albumsData?.forEach((album) => {
      map.set(album.albumId, album.title)
    })
    return map
  }, [albumsData])

  // Enrich songs with artist and album names
  const enrichedSongs = useMemo(() => {
    const songs = songsData?.map((song) => ({
      ...song,
      artistName: artistMap.get(song.artistId) || 'Không rõ',
      albumTitle: song.albumId ? albumMap.get(song.albumId) || 'Unknown' : null,
    })) || []

    // Apply search filter
    if (!searchQuery.trim()) return songs
    
    const query = searchQuery.toLowerCase()
    return songs.filter((song) => {
      return (
        song.title?.toLowerCase().includes(query) ||
        song.artistName?.toLowerCase().includes(query) ||
        song.albumTitle?.toLowerCase().includes(query) ||
        song.genre?.toLowerCase().includes(query)
      )
    })
  }, [songsData, artistMap, albumMap, searchQuery])

  // Transform API data to match existing structure
  const totalSongs = enrichedSongs?.length || 0
  const totalPages = Math.ceil(totalSongs / itemsPerPage)
  
  // Paginate data
  const startIndex = (page - 1) * itemsPerPage
  const endIndex = startIndex + itemsPerPage
  const paginatedSongs = enrichedSongs?.slice(startIndex, endIndex) || []
  
  const data = {
    data: paginatedSongs,
    total: totalSongs,
    totalPages: totalPages,
  }

  const tabs = [
    { id: 'all', label: 'All Tracks', count: data?.total || 0 },
    // { id: 'albums', label: 'Albums', count: 234 },
    // { id: 'singles', label: 'Singles', count: 567 },
    // { id: 'pending', label: 'Pending Approval', count: 23 },
  ]

  const handleSelectTrack = (trackId) => {
    setSelectedTracks(prev =>
      prev.includes(trackId)
        ? prev.filter(id => id !== trackId)
        : [...prev, trackId]
    )
  }

  const handleSelectAll = () => {
    if (selectedTracks.length === data?.data.length) {
      setSelectedTracks([])
    } else {
      setSelectedTracks(data?.data.map(track => track.songId) || [])
    }
  }

  // Open delete dialog for single track
  const handleDeleteClick = (track) => {
    setItemToDelete({ 
      type: 'single', 
      id: track.songId, 
      title: track.title 
    })
    setShowDeleteDialog(true)
  }

  // Open delete dialog for multiple tracks
  const handleBulkDeleteClick = () => {
    const tracksToDelete = data?.data.filter(track => 
      selectedTracks.includes(track.songId)
    ) || []
    
    setItemToDelete({ 
      type: 'bulk', 
      ids: selectedTracks,
      titles: tracksToDelete.map(t => t.title)
    })
    setShowDeleteDialog(true)
  }

  // Execute delete
  const handleConfirmDelete = async () => {
    setIsDeleting(true)
    try {
      if (itemToDelete.type === 'single') {
        // Delete single track
        await deleteSong(itemToDelete.id)
        toast.success('Đã xóa bài hát thành công!')
      } else {
        // Delete multiple tracks
        await Promise.all(
          itemToDelete.ids.map(id => deleteSong(id))
        )
        toast.success(`Đã xóa ${itemToDelete.ids.length} bài hát thành công!`)
        setSelectedTracks([])
      }
      
      // Refetch songs list
      queryClient.invalidateQueries(['songs'])
      
      setShowDeleteDialog(false)
    } catch (error) {
      console.error('Error deleting song(s):', error)
      toast.error('Không thể xóa bài hát. Vui lòng thử lại!')
    } finally {
      setIsDeleting(false)
    }
  }

  const handleBulkAction = (action) => {
    if (action === 'Delete') {
      handleBulkDeleteClick()
    } else {
      toast.success(`${action} applied to ${selectedTracks.length} tracks`)
      setSelectedTracks([])
    }
  }

  const handleRowClick = (trackId, e) => {
    // Don't navigate if clicking on checkbox, action button, or inside action menu
    if (
      e.target.closest('input[type="checkbox"]') ||
      e.target.closest('.action-menu') ||
      e.target.closest('button')
    ) {
      return
    }
    navigate(`/songs/${trackId}`)
  }
  
  // Toggle dropdown menu with position calculation
  const handleDropdownToggle = (trackId, event) => {
    event.stopPropagation()
    
    if (openDropdown === trackId) {
      setOpenDropdown(null)
    } else {
      const buttonRect = event.currentTarget.getBoundingClientRect()
      
      // Use viewport coordinates directly (no scrollY/scrollX needed for fixed positioning)
      // getBoundingClientRect() already returns positions relative to viewport
      setDropdownPosition({
        top: buttonRect.bottom + 4, // Add small gap below button
        right: window.innerWidth - buttonRect.right, // Align right edge of dropdown with button
      })
      setOpenDropdown(trackId)
    }
  }
  
  // Close dropdown when clicking outside
  const handleCloseDropdown = () => {
    setOpenDropdown(null)
  }

  return (
    <div className="space-y-4">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-text-primary mb-2">
            Quản lý bài hát
          </h1>
        </div>
        <div className="flex gap-2">
          <Button
            variant="secondary"
            icon={Upload}
            iconPosition="left"
            onClick={() => navigate('/songs/bulk-import')}
          >
            Nhập hàng loạt
          </Button>
          <Button
            icon={Plus}
            iconPosition="left"
            onClick={() => navigate('/songs/add')}
          >
            Thêm bài hát
          </Button>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex flex-wrap gap-2 border-b border-border">
        {tabs.map((tab) => (
          <button
            key={tab.id}
            onClick={() => setSelectedTab(tab.id)}
            className={`px-4 py-2 font-medium transition-all ${
              selectedTab === tab.id
                ? 'text-spotify-green border-b-2 border-spotify-green'
                : 'text-text-secondary hover:text-text-primary'
            }`}
          >
            {tab.label}
            <span className="ml-2 text-sm">({tab.count})</span>
          </button>
        ))}
      </div>

      {/* Filters & Bulk Actions */}
      <Card className="p-4">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <div className="flex items-center gap-3 flex-wrap flex-1">
            {/* Search */}
            <div className="relative flex-1 max-w-md">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-text-tertiary" />
              <input
                type="text"
                placeholder="Search songs..."
                value={searchQuery}
                onChange={(e) => {
                  setSearchQuery(e.target.value)
                  setPage(1)
                }}
                className="w-full pl-10 pr-4 py-2 bg-bg-secondary border border-border rounded-lg text-text-primary placeholder:text-text-tertiary focus:outline-none focus:border-spotify-green"
              />
            </div>
            
            <Button
              variant="secondary"
              size="sm"
              icon={Filter}
              onClick={() => setShowFilters(!showFilters)}
            >
              Filters
            </Button>
            {selectedTracks.length > 0 && (
              <div className="flex items-center gap-2">
                <span className="text-sm text-text-secondary">
                  {selectedTracks.length} selected
                </span>
                <Button
                  variant="secondary"
                  size="sm"
                  onClick={() => handleBulkAction('Delete')}
                  className="text-apple-red text-sm transition-colors"
                >
                  Delete
                </Button>
              </div>
            )}
          </div>
          
          {/* Page Size Selector */}
          <div className="flex items-center gap-2">
            <span className="text-sm text-text-secondary whitespace-nowrap">Per page:</span>
            <select
              value={itemsPerPage}
              onChange={(e) => {
                setItemsPerPage(Number(e.target.value))
                setPage(1)
              }}
              className="px-3 py-2 bg-bg-secondary border border-border rounded-lg text-text-primary focus:outline-none focus:border-spotify-green cursor-pointer"
            >
              <option value={10}>10</option>
              <option value={50}>50</option>
              <option value={100}>100</option>
            </select>
          </div>
        </div>

        {/* Filter Panel */}
        {/* {showFilters && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            exit={{ opacity: 0, height: 0 }}
            className="mt-4 pt-4 border-t border-border grid grid-cols-1 md:grid-cols-4 gap-4"
          >
            <Select
              label="Genre"
              options={[
                { value: '', label: 'All Genres' },
                { value: 'pop', label: 'Pop' },
                { value: 'rock', label: 'Rock' },
                { value: 'hip-hop', label: 'Hip Hop' },
                { value: 'electronic', label: 'Electronic' },
              ]}
              value={filters.genre}
              onChange={(e) => setFilters({ ...filters, genre: e.target.value })}
            />
            <Select
              label="Status"
              options={[
                { value: '', label: 'All Status' },
                { value: 'active', label: 'Active' },
                { value: 'pending', label: 'Pending' },
                { value: 'approved', label: 'Approved' },
              ]}
              value={filters.status}
              onChange={(e) => setFilters({ ...filters, status: e.target.value })}
            />
            <Select
              label="Date Range"
              options={[
                { value: '', label: 'All Time' },
                { value: 'today', label: 'Today' },
                { value: 'week', label: 'This Week' },
                { value: 'month', label: 'This Month' },
              ]}
              value={filters.dateRange}
              onChange={(e) => setFilters({ ...filters, dateRange: e.target.value })}
            />
            <div className="flex items-end gap-2">
              <Button variant="secondary" size="md" className="flex-1">
                Apply
              </Button>
              <Button
                variant="ghost"
                size="md"
                onClick={() => setFilters({ genre: '', status: '', dateRange: '' })}
              >
                Reset
              </Button>
            </div>
          </motion.div>
        )} */}
      </Card>

      {/* Tracks Table */}
      <Card className="p-0 overflow-hidden">
        {isLoading ? (
          <div className="p-6">
            <TableSkeleton rows={10} columns={8} />
          </div>
        ) : (
          <>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-bg-secondary">
                  <tr>
                    <th className="px-4 py-3">
                      <input
                        type="checkbox"
                        checked={selectedTracks.length === data?.data.length}
                        onChange={handleSelectAll}
                        className="w-4 h-4 rounded border-border bg-bg-tertiary"
                      />
                    </th>
                    <th className="px-4 py-3 pl-12 text-xs font-medium text-text-tertiary uppercase text-left">
                      Track
                    </th>
                    <th className="px-4 py-3 text-xs font-medium text-text-tertiary uppercase text-left">
                      Artist
                    </th>
                    <th className="text-left px-4 py-3 text-xs font-medium text-text-tertiary uppercase">
                      Album
                    </th>
                    <th className="text-left px-4 py-3 text-xs font-medium text-text-tertiary uppercase">
                      Genre
                    </th>
                    <th className="text-left px-4 py-3 text-xs font-medium text-text-tertiary uppercase">
                      Duration
                    </th>
                    <th className="text-left px-4 py-3 text-xs font-medium text-text-tertiary uppercase">
                      Upload Date
                    </th>
                    <th className="text-left px-4 py-3 text-xs font-medium text-text-tertiary uppercase">
                      Status
                    </th>
                    <th className="text-left px-4 py-3 text-xs font-medium text-text-tertiary uppercase">
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-border">
                  {data?.data.map((track) => {
                    const status = getStatusColor(track.isActive);
                    return (
                    <tr
                      key={track.songId}
                      onClick={(e) => handleRowClick(track.songId, e)}
                      className="hover:bg-bg-hover transition-colors group cursor-pointer"
                    >
                      <td className="px-4 py-3" onClick={(e) => e.stopPropagation()}>
                        <input
                          type="checkbox"
                          checked={selectedTracks.includes(track.songId)}
                          onChange={() => handleSelectTrack(track.songId)}
                          className="w-4 h-4 rounded border-border bg-bg-tertiary"
                        />
                      </td>
                      <td className="px-4 py-3">
                        <div className="flex items-center gap-3">
                          <div className="relative group/play flex-shrink-0">
                            <img
                              src={track.coverImageUrl || '/placeholder-song.jpg'}
                              alt={track.title}
                              className="w-12 h-12 rounded object-cover"
                              onError={(e) => {
                                e.target.src = '/placeholder-song.jpg';
                              }}
                            />
                            {/* <div className="absolute inset-0 bg-black/60 rounded flex items-center justify-center opacity-0 group-hover/play:opacity-100 transition-opacity">
                              <Play className="w-5 h-5 text-white" />
                            </div> */}
                          </div>
                          <div className="flex flex-col">
                            <button
                              onClick={(e) => {
                                e.stopPropagation()
                                navigate(`/songs/${track.songId}`)
                              }}
                              className="font-medium text-text-primary hover:text-spotify-green transition-colors text-left"
                            >
                              {track.title}
                            </button>
                          </div>
                        </div>
                      </td>
                      <td className="px-4 py-3 text-text-secondary">
                        {track.artistName}
                      </td>
                      <td className="px-4 py-3 text-text-secondary">
                        {track.albumTitle || 'Không'}
                      </td>
                      <td className="px-4 py-3 text-text-secondary">
                        {track.genre || 'Unknown'}
                      </td>
                      <td className="px-4 py-3 text-text-secondary">
                        {formatDuration(track.duration)}
                      </td>
                      {/* <td className="px-4 py-3">
                        <div className="flex items-center gap-2">
                          <span className="font-medium text-text-primary">
                            {formatNumber(track.streams)}
                          </span>
                          <TrendingUp className="w-4 h-4 text-spotify-green" />
                        </div>
                      </td> */}
                      <td className="px-4 py-3 text-text-secondary">
                        {formatDate(track.createdAt)}
                      </td>
                      <td className="px-4 py-3">
                        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border ${status.bg} ${status.text} ${status.border}`}>
                          {status.label}
                        </span>
                      </td>
                      <td className="px-4 py-3">
                        <div className="action-menu" onClick={(e) => e.stopPropagation()}>
                          <button 
                            onClick={(e) => handleDropdownToggle(track.songId, e)}
                            className="p-1.5 hover:bg-bg-hover rounded transition-colors"
                          >
                            <MoreVertical className="w-4 h-4 text-text-tertiary" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  )})}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            <div className="px-6 py-4 border-t border-border flex items-center justify-between">
              <p className="text-sm text-text-secondary">
                Showing {startIndex + 1} to {Math.min(endIndex, totalSongs)} of {totalSongs} tracks
              </p>
              <div className="flex items-center gap-2">
                <Button
                  variant="secondary"
                  size="sm"
                  disabled={page === 1}
                  onClick={() => setPage(page - 1)}
                >
                  Previous
                </Button>
                <span className="text-sm text-text-secondary px-3">
                  Page {page} of {totalPages}
                </span>
                <Button
                  variant="secondary"
                  size="sm"
                  disabled={page >= totalPages}
                  onClick={() => setPage(page + 1)}
                >
                  Next
                </Button>
              </div>
            </div>
          </>
        )}
      </Card>

      {/* Global Dropdown Menu - Fixed position, rendered outside table */}
      {openDropdown && (
        <>
          {/* Backdrop to close dropdown */}
          <div 
            className="fixed inset-0 z-30" 
            onClick={handleCloseDropdown}
          />
          
          {/* Dropdown Menu */}
          <div 
            className="fixed w-40 bg-bg-card border border-border rounded-lg shadow-xl z-40"
            style={{
              top: `${dropdownPosition.top}px`,
              right: `${dropdownPosition.right}px`,
            }}
          >
            <div className="p-1">
              <button 
                onClick={() => {
                  navigate(`/songs/${openDropdown}/edit`)
                  handleCloseDropdown()
                }}
                className="w-full flex items-center gap-3 px-3 py-2 hover:bg-bg-hover rounded text-text-secondary hover:text-text-primary text-sm transition-colors"
              >
                <Edit className="w-4 h-4" />
                Chỉnh sửa
              </button>
              <button 
                onClick={() => {
                  const track = data?.data.find(t => t.songId === openDropdown)
                  handleDeleteClick(track)
                  handleCloseDropdown()
                }}
                className="w-full flex items-center gap-3 px-3 py-2 hover:bg-bg-hover rounded text-apple-red text-sm transition-colors"
              >
                <Trash2 className="w-4 h-4" />
                Xóa
              </button>
            </div>
          </div>
        </>
      )}

      {/* Confirm Delete Dialog */}
      <ConfirmDialog
        isOpen={showDeleteDialog}
        onClose={() => setShowDeleteDialog(false)}
        onConfirm={handleConfirmDelete}
        isLoading={isDeleting}
        variant="danger"
        title={itemToDelete?.type === 'bulk' 
          ? `Xóa ${itemToDelete?.ids?.length} bài hát` 
          : 'Xóa bài hát'}
        message={
          itemToDelete?.type === 'bulk' ? (
            <div>
              <p className="text-text-secondary mb-3">
                Bạn có chắc chắn muốn xóa <strong className="text-text-primary">{itemToDelete.ids.length} bài hát</strong> đã chọn?
              </p>
              <div className="max-h-32 overflow-y-auto bg-bg-secondary rounded-lg p-3 mb-3">
                <ul className="space-y-1 text-sm">
                  {itemToDelete.titles.slice(0, 5).map((title, index) => (
                    <li key={index} className="text-text-primary">
                      • {title}
                    </li>
                  ))}
                  {itemToDelete.titles.length > 5 && (
                    <li className="text-text-tertiary italic">
                      và {itemToDelete.titles.length - 5} bài hát khác...
                    </li>
                  )}
                </ul>
              </div>
              <p className="text-red-500 text-sm">
                ⚠️ Hành động này không thể hoàn tác!
              </p>
            </div>
          ) : (
            <div>
              <p className="text-text-secondary mb-2">
                Bạn có chắc chắn muốn xóa bài hát{' '}
                <strong className="text-text-primary">&ldquo;{itemToDelete?.title}&rdquo;</strong>?
              </p>
              <p className="text-red-500 text-sm">
                Hành động này không thể hoàn tác!
              </p>
            </div>
          )
        }
        confirmText={isDeleting ? 'Đang xóa...' : 'Xóa'}
      />
    </div>
  )
}
