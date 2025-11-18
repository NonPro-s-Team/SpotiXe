import { Crown, Plus, Edit } from 'lucide-react'
import { motion } from 'framer-motion'
import Button from '@components/common/Button'
import Card from '@components/common/Card'
import Badge from '@components/common/Badge'

const plans = [
  {
    id: 'free',
    name: 'Free',
    price: 0,
    features: ['Ad-supported', 'Standard quality', 'Mobile & Desktop', 'Shuffle play'],
    users: 899788,
    color: 'secondary',
  },
  {
    id: 'premium',
    name: 'Premium',
    price: 9.99,
    features: ['Ad-free', 'High quality audio', 'Offline downloads', 'Skip unlimited'],
    users: 245890,
    color: 'primary',
    popular: true,
  },
  {
    id: 'premium-plus',
    name: 'Premium Plus',
    price: 14.99,
    features: ['Everything in Premium', 'HiFi audio', 'Early access', 'Concert tickets'],
    users: 100000,
    color: 'purple',
  },
]

export default function PremiumPlans() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-text-primary mb-2">Premium Plans</h1>
          <p className="text-text-secondary">Manage subscription tiers and pricing</p>
        </div>
        <Button icon={Plus}>Add New Plan</Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {plans.map((plan, index) => (
          <motion.div
            key={plan.id}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.1 }}
          >
            <Card className={`p-6 relative ${plan.popular ? 'border-2 border-spotify-green' : ''}`}>
              {plan.popular && (
                <Badge className="absolute -top-3 left-1/2 -translate-x-1/2" variant="primary">
                  Most Popular
                </Badge>
              )}
              
              <div className="text-center mb-6">
                <Crown className="w-12 h-12 mx-auto mb-4 text-spotify-green" />
                <h3 className="text-2xl font-bold text-text-primary mb-2">{plan.name}</h3>
                <div className="flex items-baseline justify-center gap-1">
                  <span className="text-4xl font-bold text-gradient">${plan.price}</span>
                  <span className="text-text-tertiary">/month</span>
                </div>
              </div>

              <ul className="space-y-3 mb-6">
                {plan.features.map((feature, i) => (
                  <li key={i} className="flex items-center gap-2 text-text-secondary">
                    <svg className="w-5 h-5 text-spotify-green flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                      <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                    </svg>
                    {feature}
                  </li>
                ))}
              </ul>

              <div className="p-4 bg-bg-hover rounded-lg mb-4">
                <p className="text-sm text-text-tertiary">Active Users</p>
                <p className="text-2xl font-bold text-text-primary">{plan.users.toLocaleString()}</p>
              </div>

              <Button variant="secondary" icon={Edit} className="w-full">
                Edit Plan
              </Button>
            </Card>
          </motion.div>
        ))}
      </div>
    </div>
  )
}
