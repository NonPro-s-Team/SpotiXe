import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Search, Download, Mail, Ban, Eye } from 'lucide-react'
import { format } from 'date-fns'
import Button from '@components/common/Button'
import Card from '@components/common/Card'
import Avatar from '@components/common/Avatar'
import { StatusBadge } from '@components/common/Badge'
import { Select } from '@components/common/Input'
import Modal from '@components/common/Modal'
import { TableSkeleton } from '@components/common/LoadingScreen'
import mockApi from '@services/mockApi'
import { downloadCSV } from '@utils/helpers'

export default function UsersManagement() {
  const [page, setPage] = useState(1)
  const [selectedUser, setSelectedUser] = useState(null)
  const [showUserModal, setShowUserModal] = useState(false)

  const { data, isLoading } = useQuery({
    queryKey: ['users', page],
    queryFn: () => mockApi.getUsers(page, 20),
  })

  const handleExportUsers = () => {
    if (data?.data) {
      downloadCSV(data.data, 'spotixe-users.csv')
    }
  }

  const planBadgeColor = {
    free: 'secondary',
    premium: 'purple',
    'premium-plus': 'primary',
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
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div className="relative md:col-span-2">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-text-tertiary" />
            <input
              type="text"
              placeholder="Search users..."
              className="w-full pl-10 pr-4 py-2 bg-bg-secondary border border-border rounded-lg focus:border-spotify-green focus:outline-none"
            />
          </div>
          <Select
            options={[
              { value: '', label: 'All Plans' },
              { value: 'free', label: 'Free' },
              { value: 'premium', label: 'Premium' },
              { value: 'premium-plus', label: 'Premium Plus' },
            ]}
          />
          <Select
            options={[
              { value: '', label: 'All Status' },
              { value: 'active', label: 'Active' },
              { value: 'inactive', label: 'Inactive' },
            ]}
          />
        </div>
      </Card>

      {/* Users Table */}
      <Card className="p-0 overflow-hidden">
        {isLoading ? (
          <div className="p-6">
            <TableSkeleton rows={10} columns={7} />
          </div>
        ) : (
          <>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-bg-secondary">
                  <tr>
                    <th className="text-left px-6 py-4 text-xs font-medium text-text-tertiary uppercase">
                      User
                    </th>
                    <th className="text-left px-6 py-4 text-xs font-medium text-text-tertiary uppercase">
                      Email
                    </th>
                    <th className="text-left px-6 py-4 text-xs font-medium text-text-tertiary uppercase">
                      Plan
                    </th>
                    <th className="text-left px-6 py-4 text-xs font-medium text-text-tertiary uppercase">
                      Status
                    </th>
                    <th className="text-left px-6 py-4 text-xs font-medium text-text-tertiary uppercase">
                      Join Date
                    </th>
                    <th className="text-left px-6 py-4 text-xs font-medium text-text-tertiary uppercase">
                      Last Active
                    </th>
                    <th className="text-left px-6 py-4 text-xs font-medium text-text-tertiary uppercase">
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-border">
                  {data?.data.map((user) => (
                    <tr key={user.id} className="hover:bg-bg-hover">
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-3">
                          <Avatar src={user.avatar} name={user.name} />
                          <span className="font-medium text-text-primary">
                            {user.name}
                          </span>
                        </div>
                      </td>
                      <td className="px-6 py-4 text-text-secondary">
                        {user.email}
                      </td>
                      <td className="px-6 py-4">
                        <StatusBadge status={user.plan} />
                      </td>
                      <td className="px-6 py-4">
                        <StatusBadge status={user.status} />
                      </td>
                      <td className="px-6 py-4 text-text-secondary">
                        {format(new Date(user.joinDate), 'MMM d, yyyy')}
                      </td>
                      <td className="px-6 py-4 text-text-secondary">
                        {format(new Date(user.lastActive), 'MMM d, yyyy')}
                      </td>
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-2">
                          <button
                            onClick={() => {
                              setSelectedUser(user)
                              setShowUserModal(true)
                            }}
                            className="p-2 hover:bg-bg-secondary rounded"
                          >
                            <Eye className="w-4 h-4 text-text-secondary" />
                          </button>
                          <button className="p-2 hover:bg-bg-secondary rounded">
                            <Mail className="w-4 h-4 text-text-secondary" />
                          </button>
                          <button className="p-2 hover:bg-bg-secondary rounded">
                            <Ban className="w-4 h-4 text-apple-red" />
                          </button>
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
                Showing {((page - 1) * 20) + 1} to {Math.min(page * 20, data?.total || 0)} of {data?.total || 0} users
              </p>
              <div className="flex gap-2">
                <Button
                  variant="secondary"
                  size="sm"
                  disabled={page === 1}
                  onClick={() => setPage(page - 1)}
                >
                  Previous
                </Button>
                <Button
                  variant="secondary"
                  size="sm"
                  disabled={page >= (data?.totalPages || 1)}
                  onClick={() => setPage(page + 1)}
                >
                  Next
                </Button>
              </div>
            </div>
          </>
        )}
      </Card>

      {/* User Detail Modal */}
      <Modal
        isOpen={showUserModal}
        onClose={() => setShowUserModal(false)}
        title="User Details"
        size="lg"
      >
        {selectedUser && (
          <div className="space-y-6">
            <div className="flex items-center gap-4">
              <Avatar src={selectedUser.avatar} name={selectedUser.name} size="xl" />
              <div>
                <h3 className="text-xl font-semibold text-text-primary">
                  {selectedUser.name}
                </h3>
                <p className="text-text-secondary">{selectedUser.email}</p>
                <div className="flex gap-2 mt-2">
                  <StatusBadge status={selectedUser.plan} />
                  <StatusBadge status={selectedUser.status} />
                </div>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="p-4 bg-bg-hover rounded-lg">
                <p className="text-sm text-text-tertiary mb-1">Join Date</p>
                <p className="font-medium text-text-primary">
                  {format(new Date(selectedUser.joinDate), 'MMMM d, yyyy')}
                </p>
              </div>
              <div className="p-4 bg-bg-hover rounded-lg">
                <p className="text-sm text-text-tertiary mb-1">Last Active</p>
                <p className="font-medium text-text-primary">
                  {format(new Date(selectedUser.lastActive), 'MMMM d, yyyy')}
                </p>
              </div>
            </div>

            <div className="flex gap-2">
              <Button className="flex-1">Send Email</Button>
              <Button variant="secondary" className="flex-1">Edit User</Button>
              <Button variant="danger">Suspend</Button>
            </div>
          </div>
        )}
      </Modal>
    </div>
  )
}
