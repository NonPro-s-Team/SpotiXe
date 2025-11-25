import { useState, useMemo } from 'react'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { Search, Download, UserCheck, UserX } from 'lucide-react'
import Button from '@components/common/Button'
import Card from '@components/common/Card'
import { TableSkeleton } from '@components/common/LoadingScreen'
import { downloadCSV } from '@utils/helpers'
import toast from 'react-hot-toast'
import {
  getRegularUsers,
  getFirebaseUsers,
  mergeUserData,
  disableRegularUser,
  enableRegularUser,
  disableFirebaseUser,
  enableFirebaseUser,
} from '@services/api/userService'

export default function UsersManagement() {
  const queryClient = useQueryClient()
  const [page, setPage] = useState(1)
  const [itemsPerPage, setItemsPerPage] = useState(10)
  const [searchQuery, setSearchQuery] = useState('')
  const [isTogglingStatus, setIsTogglingStatus] = useState({})

  // Fetch regular users (Email + OTP)
  const { data: regularUsersData, isLoading: isLoadingRegular } = useQuery({
    queryKey: ['regularUsers'],
    queryFn: getRegularUsers,
    staleTime: 5 * 60 * 1000,
    cacheTime: 10 * 60 * 1000,
  })

  // Fetch Firebase users (Google login)
  const { data: firebaseUsersData, isLoading: isLoadingFirebase } = useQuery({
    queryKey: ['firebaseUsers'],
    queryFn: getFirebaseUsers,
    staleTime: 5 * 60 * 1000,
    cacheTime: 10 * 60 * 1000,
  })

  const isLoading = isLoadingRegular || isLoadingFirebase

  // Merge and filter users
  const mergedUsers = useMemo(() => {
    const merged = mergeUserData(regularUsersData, firebaseUsersData)
    
    // Apply search filter
    if (!searchQuery.trim()) return merged
    
    const query = searchQuery.toLowerCase()
    return merged.filter((user) => {
      return (
        user.username?.toLowerCase().includes(query) ||
        user.email?.toLowerCase().includes(query)
      )
    })
  }, [regularUsersData, firebaseUsersData, searchQuery])

  // Calculate pagination
  const totalUsers = mergedUsers.length
  const totalPages = Math.ceil(totalUsers / itemsPerPage)
  const startIndex = (page - 1) * itemsPerPage
  const endIndex = startIndex + itemsPerPage
  const paginatedUsers = mergedUsers.slice(startIndex, endIndex)

  const handleExportUsers = () => {
    if (mergedUsers.length > 0) {
      const exportData = mergedUsers.map(user => ({
        Username: user.username,
        Email: user.email,
        Status: user.isActive ? 'Active' : 'Inactive',
        Type: user.type === 'email' ? 'Email + OTP' : 'Google (Firebase)',
      }))
      downloadCSV(exportData, 'spotixe-users.csv')
      toast.success('Users exported successfully!')
    }
  }

  // Handle enable/disable user
  const handleToggleUserStatus = async (user) => {
    const userId = user.type === 'firebase' ? user.firebaseUid : user.id
    setIsTogglingStatus(prev => ({ ...prev, [userId]: true }))

    try {
      if (user.type === 'email') {
        // Handle Email + OTP user
        if (user.isActive) {
          await disableRegularUser(user.id)
          toast.success('User disabled successfully!')
        } else {
          await enableRegularUser(user.id)
          toast.success('User enabled successfully!')
        }
      } else {
        // Handle Firebase user
        if (user.isActive) {
          await disableFirebaseUser(user.firebaseUid)
          toast.success('Firebase user disabled successfully!')
        } else {
          await enableFirebaseUser(user.firebaseUid)
          toast.success('Firebase user enabled successfully!')
        }
      }

      // Refetch data
      queryClient.invalidateQueries(['regularUsers'])
      queryClient.invalidateQueries(['firebaseUsers'])
    } catch (error) {
      console.error('Error toggling user status:', error)
      toast.error('Failed to update user status. Please try again.')
    } finally {
      setIsTogglingStatus(prev => ({ ...prev, [userId]: false }))
    }
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-text-primary mb-2">
            Users Management
          </h1>
          {/* <p className="text-text-secondary">
            Manage all user accounts and subscriptions
          </p> */}
        </div>
        <Button icon={Download} onClick={handleExportUsers}>
          Export Users
        </Button>
      </div>

      {/* Filters */}
      <Card className="p-4">
        <div className="flex flex-col md:flex-row md:items-center gap-4">
          {/* Search */}
          <div className="relative flex-1 max-w-md">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-text-tertiary" />
            <input
              type="text"
              placeholder="Search users..."
              value={searchQuery}
              onChange={(e) => {
                setSearchQuery(e.target.value)
                setPage(1)
              }}
              className="w-full pl-10 pr-4 py-2 bg-bg-secondary border border-border rounded-lg text-text-primary placeholder:text-text-tertiary focus:outline-none focus:border-spotify-green"
            />
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
      </Card>

      {/* Users Table */}
      <Card className="p-0 overflow-hidden">
        {isLoading ? (
          <div className="p-6">
            <TableSkeleton rows={10} columns={5} />
          </div>
        ) : (
          <>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-bg-secondary">
                  <tr>
                    <th className="text-left px-6 py-4 text-xs font-medium text-text-tertiary uppercase">
                      Logo
                    </th>
                    <th className="text-left px-6 py-4 text-xs font-medium text-text-tertiary uppercase">
                      Username
                    </th>
                    <th className="text-left px-6 py-4 text-xs font-medium text-text-tertiary uppercase">
                      Email
                    </th>
                    <th className="text-left px-6 py-4 text-xs font-medium text-text-tertiary uppercase">
                      Status
                    </th>
                    <th className="text-left px-6 py-4 text-xs font-medium text-text-tertiary uppercase">
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-border">
                  {paginatedUsers.length === 0 ? (
                    <tr>
                      <td colSpan="5" className="px-6 py-12 text-center">
                        <p className="text-text-secondary">No users found</p>
                      </td>
                    </tr>
                  ) : (
                    paginatedUsers.map((user) => {
                      const userId = user.type === 'firebase' ? user.firebaseUid : user.id
                      const isToggling = isTogglingStatus[userId]
                      
                      return (
                        <tr key={`${user.type}-${user.id}`} className="hover:bg-bg-hover">
                          <td className="px-6 py-4">
                            <img
                              src={user.logo}
                              alt={user.type === 'email' ? 'Email + OTP' : 'Firebase'}
                              className="w-8 h-8 object-contain"
                              title={user.type === 'email' ? 'Email + OTP' : 'Google (Firebase)'}
                            />
                          </td>
                          <td className="px-6 py-4">
                            <span className="font-medium text-text-primary">
                              {user.username}
                            </span>
                          </td>
                          <td className="px-6 py-4 text-text-secondary">
                            {user.email}
                          </td>
                          <td className="px-6 py-4">
                            <span
                              className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                                user.isActive
                                  ? 'bg-green-500/10 text-green-500 border border-green-500/20'
                                  : 'bg-red-500/10 text-red-500 border border-red-500/20'
                              }`}
                            >
                              {user.isActive ? 'Active' : 'Inactive'}
                            </span>
                          </td>
                          <td className="px-6 py-4">
                            <Button
                              size="sm"
                              variant={user.isActive ? 'danger' : 'primary'}
                              icon={user.isActive ? UserX : UserCheck}
                              onClick={() => handleToggleUserStatus(user)}
                              disabled={isToggling}
                            >
                              {isToggling ? 'Processing...' : user.isActive ? 'Disable' : 'Enable'}
                            </Button>
                          </td>
                        </tr>
                      )
                    })
                  )}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            <div className="px-6 py-4 border-t border-border flex items-center justify-between">
              <p className="text-sm text-text-secondary">
                Showing {startIndex + 1} to {Math.min(endIndex, totalUsers)} of {totalUsers} users
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
                  Page {page} of {totalPages || 1}
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

    </div>
  )
}
