import { useState } from 'react'
import { Settings as SettingsIcon, Save } from 'lucide-react'
import Button from '@components/common/Button'
import Card, { CardHeader, CardTitle, CardContent } from '@components/common/Card'
import Input, { Textarea, Select } from '@components/common/Input'
import toast from 'react-hot-toast'

export default function Settings() {
  const [activeTab, setActiveTab] = useState('general')

  const tabs = [
    { id: 'general', label: 'General Settings' },
    { id: 'appearance', label: 'Appearance' },
    { id: 'audio', label: 'Audio Settings' },
    { id: 'payment', label: 'Payment Gateway' },
    { id: 'email', label: 'Email Templates' },
    { id: 'api', label: 'API Keys' },
    { id: 'security', label: 'Security' },
  ]

  const handleSave = () => {
    toast.success('Settings saved successfully!')
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-text-primary mb-2">Settings</h1>
          <p className="text-text-secondary">Manage your platform configuration</p>
        </div>
        <Button icon={Save} onClick={handleSave}>Save Changes</Button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        {/* Sidebar Tabs */}
        <Card className="p-4 lg:col-span-1 h-fit">
          <nav className="space-y-1">
            {tabs.map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`w-full text-left px-4 py-2 rounded-lg transition-colors ${
                  activeTab === tab.id
                    ? 'bg-gradient-primary text-white'
                    : 'text-text-secondary hover:bg-bg-hover'
                }`}
              >
                {tab.label}
              </button>
            ))}
          </nav>
        </Card>

        {/* Content Area */}
        <div className="lg:col-span-3 space-y-6">
          {activeTab === 'general' && (
            <Card>
              <CardHeader>
                <CardTitle>Cài đặt chung</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <Input label="Tên ứng dụng" defaultValue="SpotiXe" />
                <Textarea label="Mô tả" defaultValue="Nền tảng phát nhạc cao cấp" />
                <Input label="Email hỗ trợ" type="email" defaultValue="support@spotixe.io.vn" />
                <Input label="Liên hệ" type="tel" defaultValue="+85 222 7725" />
              </CardContent>
            </Card>
          )}

          {activeTab === 'appearance' && (
            <Card>
              <CardHeader>
                <CardTitle>Cài đặt giao diện</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <Select
                  label="Chủ đề mặc định"
                  options={[
                    { value: 'dark', label: 'Chế độ tối' },
                    { value: 'light', label: 'Chế độ sáng' },
                    { value: 'auto', label: 'Tự động (Hệ thống)' },
                  ]}
                />
                <Input label="Màu chính" type="color" defaultValue="#1DB954" />
                <Input label="Màu phụ" type="color" defaultValue="#0A84FF" />
              </CardContent>
            </Card>
          )}

          {activeTab === 'audio' && (
            <Card>
              <CardHeader>
                <CardTitle>Cài đặt âm thanh</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <Select
                  label="Chất lượng mặc định"
                  options={[
                    { value: 'low', label: 'Thấp (96 kbps)' },
                    { value: 'normal', label: 'Bình thường (160 kbps)' },
                    { value: 'high', label: 'Cao (320 kbps)' },
                    { value: 'hifi', label: 'HiFi (Lossless)' },
                  ]}
                />
                <Select
                  label="Giao thức phát trực tuyến"
                  options={[
                    { value: 'hls', label: 'HLS (HTTP Live Streaming)' },
                    { value: 'dash', label: 'DASH (Dynamic Adaptive Streaming)' },
                  ]}
                />
              </CardContent>
            </Card>
          )}

          {activeTab === 'payment' && (
            <Card>
              <CardHeader>
                <CardTitle>Cấu hình cổng thanh toán</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <Input label="Stripe API Key" type="password" placeholder="sk_live_..." />
                <Input label="PayPal Client ID" type="password" placeholder="Nhập PayPal Client ID" />
                <Select
                  label="Default Currency"
                  options={[
                    { value: 'usd', label: 'USD - Đô la Mỹ' },
                    { value: 'eur', label: 'EUR - Euro' },
                    { value: 'gbp', label: 'GBP - Bảng Anh' },
                    { value: 'vnd', label: 'VND - Việt Nam Đồng' },
                  ]}
                />
              </CardContent>
            </Card>
          )}

          {activeTab === 'security' && (
            <Card>
              <CardHeader>
                <CardTitle>Cài đặt bảo mật</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex items-center justify-between p-4 bg-bg-hover rounded-lg">
                  <div>
                    <p className="font-medium text-text-primary">Xác thực hai yếu tố</p>
                    <p className="text-sm text-text-secondary">Thêm một lớp bảo mật bổ sung</p>
                  </div>
                  <label className="relative inline-flex items-center cursor-pointer">
                    <input type="checkbox" className="sr-only peer" />
                    <div className="w-11 h-6 bg-bg-tertiary peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-spotify-green"></div>
                  </label>
                </div>
                <Input label="Thời gian phiên (phút)" type="number" defaultValue="30" />
                <Textarea label="Danh sách trắng IP" placeholder="Nhập địa chỉ IP (mỗi địa chỉ một dòng)" />
              </CardContent>
            </Card>
          )}
        </div>
      </div>
    </div>
  )
}
