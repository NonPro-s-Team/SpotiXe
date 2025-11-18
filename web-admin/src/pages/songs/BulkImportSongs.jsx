import { useState, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { Upload, Download, FileText, AlertCircle, CheckCircle, XCircle, ArrowLeft } from 'lucide-react'
import { motion, AnimatePresence } from 'framer-motion'
import Button from '@components/common/Button'
import Card from '@components/common/Card'
import LeavePageDialog from '@components/common/LeavePageDialog'
import { bulkImportSongs } from '@services/api/songService'
import { downloadCSVTemplate, parseCSVFile } from '@utils/csvHelper'
import toast from 'react-hot-toast'

export default function BulkImportSongs() {
  const navigate = useNavigate()
  const [selectedFile, setSelectedFile] = useState(null)
  const [isProcessing, setIsProcessing] = useState(false)
  const [importResults, setImportResults] = useState(null)
  const [dragActive, setDragActive] = useState(false)
  const [processingStatus, setProcessingStatus] = useState('')
  const abortControllerRef = useRef(null)

  // Handle cleanup when user confirms leaving page
  const handleConfirmLeave = () => {
    // Cleanup when user confirms leaving
    if (abortControllerRef.current) {
      abortControllerRef.current.abort()
    }
    setIsProcessing(false)
    toast.error('Đã hủy tiến trình nhập bài hát')
  }

  const handleDrag = (e) => {
    e.preventDefault()
    e.stopPropagation()
    if (e.type === "dragenter" || e.type === "dragover") {
      setDragActive(true)
    } else if (e.type === "dragleave") {
      setDragActive(false)
    }
  }

  const handleDrop = (e) => {
    e.preventDefault()
    e.stopPropagation()
    setDragActive(false)
    
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      handleFileSelect(e.dataTransfer.files[0])
    }
  }

  const handleFileSelect = (file) => {
    if (file && file.type === 'text/csv') {
      setSelectedFile(file)
      setImportResults(null)
    } else {
      toast.error('Vui lòng chọn file CSV hợp lệ!')
    }
  }

  const handleFileInput = (e) => {
    if (e.target.files && e.target.files[0]) {
      handleFileSelect(e.target.files[0])
    }
  }

  const handleDownloadTemplate = () => {
    downloadCSVTemplate()
    toast.success('Đã tải xuống template CSV!')
  }

  const handleImport = async () => {
    if (!selectedFile) {
      toast.error('Vui lòng chọn file CSV!')
      return
    }

    // Create abort controller for this import session
    abortControllerRef.current = new AbortController()

    setIsProcessing(true)
    setProcessingStatus('Đang đọc file CSV...')
    
    try {
      // Parse CSV file
      const parsedData = await parseCSVFile(selectedFile)
      
      if (!parsedData || parsedData.length === 0) {
        toast.error('File CSV không có dữ liệu!')
        setIsProcessing(false)
        setProcessingStatus('')
        return
      }

      setProcessingStatus(`Đang trích xuất metadata và nhập ${parsedData.length} bài hát...`)
      
      // Call bulk import API with abort signal
      const results = await bulkImportSongs(parsedData, abortControllerRef.current.signal)
      
      // Check if process was aborted
      if (abortControllerRef.current?.signal.aborted) {
        toast.error('Tiến trình đã bị hủy')
        return
      }

      setImportResults(results)
      setProcessingStatus('')
      
      if (results.successful.length > 0) {
        toast.success(`Đã nhập thành công ${results.successful.length} bài hát!`)
      }
      if (results.failed.length > 0) {
        toast.error(`${results.failed.length} bài hát nhập thất bại!`)
      }
    } catch (error) {
      if (error.name === 'AbortError' || error.message === 'ABORTED' || error.code === 'ERR_CANCELED') {
        console.log('[Bulk Import] Import was cancelled by user')
        toast.error('Tiến trình đã bị hủy')
        setImportResults({
          successful: [],
          failed: [{
            title: 'Đã hủy',
            artistName: 'N/A',
            error: 'Tiến trình đã bị hủy bởi người dùng'
          }]
        })
      } else {
        console.error('Error importing songs:', error)
        toast.error('Có lỗi xảy ra khi nhập bài hát!')
      }
      setProcessingStatus('')
    } finally {
      setIsProcessing(false)
      abortControllerRef.current = null
    }
  }

  const handleReset = () => {
    setSelectedFile(null)
    setImportResults(null)
  }

  const getTotalSongs = () => {
    if (!importResults) return 0
    return importResults.successful.length + importResults.failed.length
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Button
            variant="ghost"
            size="sm"
            icon={ArrowLeft}
            onClick={() => navigate('/songs')}
          >
            Quay lại
          </Button>
          <div>
            <h1 className="text-3xl font-bold text-text-primary">
              Nhập bài hát hàng loạt
            </h1>
            <p className="text-text-secondary mt-1">
              Tải lên file CSV để nhập nhiều bài hát cùng lúc
            </p>
          </div>
        </div>
      </div>

      {/* Instructions Card */}
      <Card className="p-6">
        <div className="flex items-start gap-4">
          <div className="p-3 bg-spotify-green/10 rounded-lg">
            <FileText className="w-6 h-6 text-spotify-green" />
          </div>
          <div className="flex-1">
            <h3 className="text-lg font-semibold text-text-primary mb-2">
              Hướng dẫn sử dụng
            </h3>
            <ul className="space-y-2 text-text-secondary">
              <li className="flex items-start gap-2">
                <span className="text-spotify-green mt-1">•</span>
                <span>Tải xuống file template CSV bên dưới</span>
              </li>
              <li className="flex items-start gap-2">
                <span className="text-spotify-green mt-1">•</span>
                <span>Chỉ cần điền 2 cột: <strong>AudioFileUrl</strong> (URL file MP3) và <strong>CoverImageUrl</strong> (URL ảnh bìa)</span>
              </li>
              <li className="flex items-start gap-2">
                <span className="text-spotify-green mt-1">•</span>
                <span>Hệ thống sẽ <strong>tự động trích xuất metadata</strong> từ file MP3 (tên bài hát, ca sĩ, album, thời lượng, năm phát hành, thể loại)</span>
              </li>
              <li className="flex items-start gap-2">
                <span className="text-spotify-green mt-1">•</span>
                <span>Hỗ trợ <strong>nhiều ca sĩ</strong> (feat, ft, /, &amp;) - Ví dụ: &ldquo;B Ray / ASTRA&rdquo; sẽ tạo cả 2 ca sĩ nếu chưa tồn tại</span>
              </li>
              <li className="flex items-start gap-2">
                <span className="text-spotify-green mt-1">•</span>
                <span>Tất cả ca sĩ chưa tồn tại sẽ được tự động tạo mới trong hệ thống</span>
              </li>
              <li className="flex items-start gap-2">
                <span className="text-spotify-green mt-1">•</span>
                <span>Nếu album không tồn tại, bài hát sẽ không được gán vào album nào</span>
              </li>
              <li className="flex items-start gap-2">
                <span className="text-spotify-green mt-1">•</span>
                <span>Tải lên file CSV và nhấn &ldquo;Bắt đầu nhập&rdquo; để xử lý hàng loạt</span>
              </li>
            </ul>
          </div>
        </div>
      </Card>

      {/* Download Template */}
      <Card className="p-6">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="text-lg font-semibold text-text-primary mb-1">
              Tải xuống template CSV
            </h3>
            <p className="text-text-secondary text-sm">
              File mẫu chỉ có 2 cột: AudioFileUrl và CoverImageUrl. Metadata sẽ được trích xuất tự động.
            </p>
          </div>
          <Button
            variant="secondary"
            icon={Download}
            onClick={handleDownloadTemplate}
          >
            Tải template
          </Button>
        </div>
      </Card>

      {/* Upload Area */}
      <Card className="p-6">
        <h3 className="text-lg font-semibold text-text-primary mb-4">
          Tải lên file CSV
        </h3>
        
        <div
          className={`relative border-2 border-dashed rounded-lg p-8 transition-colors ${
            dragActive
              ? 'border-spotify-green bg-spotify-green/5'
              : 'border-border hover:border-spotify-green/50'
          }`}
          onDragEnter={handleDrag}
          onDragLeave={handleDrag}
          onDragOver={handleDrag}
          onDrop={handleDrop}
        >
          <div className="flex flex-col items-center justify-center text-center">
            <div className="p-4 bg-bg-secondary rounded-full mb-4">
              <Upload className="w-8 h-8 text-text-tertiary" />
            </div>
            
            {selectedFile ? (
              <div className="space-y-2">
                <p className="text-text-primary font-medium">
                  {selectedFile.name}
                </p>
                <p className="text-text-secondary text-sm">
                  {(selectedFile.size / 1024).toFixed(2)} KB
                </p>
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={handleReset}
                >
                  Chọn file khác
                </Button>
              </div>
            ) : (
              <>
                <p className="text-text-primary font-medium mb-2">
                  Kéo thả file CSV vào đây hoặc
                </p>
                <label htmlFor="csv-upload" className="cursor-pointer">
                  <Button
                    variant="secondary"
                    size="sm"
                    as="span"
                  >
                    Chọn file từ máy tính
                  </Button>
                  <input
                    id="csv-upload"
                    type="file"
                    accept=".csv"
                    onChange={handleFileInput}
                    className="hidden"
                  />
                </label>
                <p className="text-text-tertiary text-sm mt-2">
                  Chỉ chấp nhận file .csv
                </p>
              </>
            )}
          </div>
        </div>

        {/* Import Button */}
        {selectedFile && !importResults && (
          <div className="mt-6">
            {isProcessing && processingStatus && (
              <div className="mb-3 p-3 bg-blue-500/10 border border-blue-500/20 rounded-lg">
                <p className="text-blue-400 text-sm flex items-center gap-2">
                  <span className="animate-spin">⏳</span>
                  {processingStatus}
                </p>
              </div>
            )}
            <div className="flex justify-end">
              <Button
                onClick={handleImport}
                disabled={isProcessing}
                loading={isProcessing}
              >
                {isProcessing ? 'Đang xử lý...' : 'Bắt đầu nhập'}
              </Button>
            </div>
          </div>
        )}
      </Card>

      {/* Import Results */}
      <AnimatePresence>
        {importResults && (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
          >
            <Card className="p-6">
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-lg font-semibold text-text-primary">
                  Kết quả nhập
                </h3>
                <div className="flex gap-2">
                  <Button
                    variant="secondary"
                    size="sm"
                    onClick={handleReset}
                  >
                    Nhập file mới
                  </Button>
                  <Button
                    size="sm"
                    onClick={() => navigate('/songs')}
                  >
                    Xem danh sách bài hát
                  </Button>
                </div>
              </div>

              {/* Summary Stats */}
              <div className="grid grid-cols-3 gap-4 mb-6">
                <div className="bg-bg-secondary p-4 rounded-lg">
                  <div className="flex items-center gap-3">
                    <div className="p-2 bg-blue-500/10 rounded-lg">
                      <FileText className="w-5 h-5 text-blue-500" />
                    </div>
                    <div>
                      <p className="text-2xl font-bold text-text-primary">
                        {getTotalSongs()}
                      </p>
                      <p className="text-text-secondary text-sm">
                        Tổng số bài hát
                      </p>
                    </div>
                  </div>
                </div>

                <div className="bg-bg-secondary p-4 rounded-lg">
                  <div className="flex items-center gap-3">
                    <div className="p-2 bg-green-500/10 rounded-lg">
                      <CheckCircle className="w-5 h-5 text-green-500" />
                    </div>
                    <div>
                      <p className="text-2xl font-bold text-green-500">
                        {importResults.successful.length}
                      </p>
                      <p className="text-text-secondary text-sm">
                        Thành công
                      </p>
                    </div>
                  </div>
                </div>

                <div className="bg-bg-secondary p-4 rounded-lg">
                  <div className="flex items-center gap-3">
                    <div className="p-2 bg-red-500/10 rounded-lg">
                      <XCircle className="w-5 h-5 text-red-500" />
                    </div>
                    <div>
                      <p className="text-2xl font-bold text-red-500">
                        {importResults.failed.length}
                      </p>
                      <p className="text-text-secondary text-sm">
                        Thất bại
                      </p>
                    </div>
                  </div>
                </div>
              </div>

              {/* Successful Imports */}
              {importResults.successful.length > 0 && (
                <div className="mb-6">
                  <h4 className="text-md font-semibold text-green-500 mb-3 flex items-center gap-2">
                    <CheckCircle className="w-5 h-5" />
                    Nhập thành công ({importResults.successful.length})
                  </h4>
                  <div className="bg-bg-secondary rounded-lg max-h-60 overflow-y-auto">
                    <table className="w-full">
                      <thead className="bg-bg-tertiary sticky top-0">
                        <tr>
                          <th className="px-4 py-3 text-left text-xs font-medium text-text-tertiary uppercase">
                            Tên bài hát
                          </th>
                          <th className="px-4 py-3 text-left text-xs font-medium text-text-tertiary uppercase">
                            Ca sĩ
                          </th>
                          <th className="px-4 py-3 text-left text-xs font-medium text-text-tertiary uppercase">
                            Album
                          </th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-border">
                        {importResults.successful.map((song, index) => (
                          <tr key={index} className="hover:bg-bg-hover">
                            <td className="px-4 py-3 text-text-primary">
                              {song.title}
                            </td>
                            <td className="px-4 py-3 text-text-secondary">
                              {song.artistName}
                            </td>
                            <td className="px-4 py-3 text-text-secondary">
                              {song.albumTitle || 'Không có'}
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              )}

              {/* Failed Imports */}
              {importResults.failed.length > 0 && (
                <div>
                  <h4 className="text-md font-semibold text-red-500 mb-3 flex items-center gap-2">
                    <AlertCircle className="w-5 h-5" />
                    Nhập thất bại ({importResults.failed.length})
                  </h4>
                  <div className="bg-bg-secondary rounded-lg max-h-60 overflow-y-auto">
                    <table className="w-full">
                      <thead className="bg-bg-tertiary sticky top-0">
                        <tr>
                          <th className="px-4 py-3 text-left text-xs font-medium text-text-tertiary uppercase">
                            Tên bài hát
                          </th>
                          <th className="px-4 py-3 text-left text-xs font-medium text-text-tertiary uppercase">
                            Ca sĩ
                          </th>
                          <th className="px-4 py-3 text-left text-xs font-medium text-text-tertiary uppercase">
                            Lỗi
                          </th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-border">
                        {importResults.failed.map((item, index) => (
                          <tr key={index} className="hover:bg-bg-hover">
                            <td className="px-4 py-3 text-text-primary">
                              {item.title}
                            </td>
                            <td className="px-4 py-3 text-text-secondary">
                              {item.artistName}
                            </td>
                            <td className="px-4 py-3 text-red-400 text-sm">
                              {item.error}
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              )}
            </Card>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Leave Page Warning Dialog */}
      <LeavePageDialog 
        isProcessing={isProcessing}
        onConfirmLeave={handleConfirmLeave}
      />
    </div>
  )
}
