import { FileMusic } from 'lucide-react'

export default function EmptyState({
  icon: Icon = FileMusic,
  title,
  description,
  action,
}) {
  return (
    <div className="flex flex-col items-center justify-center py-12 px-4 text-center">
      <div className="w-16 h-16 rounded-full bg-bg-hover flex items-center justify-center mb-4">
        <Icon className="w-8 h-8 text-text-tertiary" />
      </div>
      <h3 className="text-lg font-semibold text-text-primary mb-2">
        {title}
      </h3>
      {description && (
        <p className="text-sm text-text-secondary mb-6 max-w-md">
          {description}
        </p>
      )}
      {action}
    </div>
  )
}
