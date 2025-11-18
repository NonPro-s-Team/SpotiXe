import { cn, getStatusColor } from '@utils/helpers'

export default function Badge({
  children,
  variant = 'default',
  size = 'md',
  className,
  ...props
}) {
  const variants = {
    default: 'bg-bg-hover text-text-primary',
    primary: 'bg-spotify-green text-white',
    secondary: 'bg-blue-500 text-white',
    success: 'bg-spotify-green text-white',
    warning: 'bg-accent-yellow text-bg-primary',
    danger: 'bg-apple-red text-white',
    purple: 'bg-purple text-white',
  }

  const sizes = {
    sm: 'px-2 py-0.5 text-xs',
    md: 'px-2.5 py-1 text-sm',
    lg: 'px-3 py-1.5 text-base',
  }

  return (
    <span
      className={cn(
        'inline-flex items-center font-medium rounded-full',
        variants[variant],
        sizes[size],
        className
      )}
      {...props}
    >
      {children}
    </span>
  )
}

export function StatusBadge({ status, ...props }) {
  return (
    <Badge
      className={getStatusColor(status)}
      {...props}
    >
      {status}
    </Badge>
  )
}
