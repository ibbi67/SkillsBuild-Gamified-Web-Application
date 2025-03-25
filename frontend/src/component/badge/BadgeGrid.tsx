import { Badge } from '@/types/databaseTypes';
import { BadgeCard } from './BadgeCard';

interface BadgeGridProps {
  badges: Badge[];
  earnedBadges: Badge[];
  progressMap?: Record<number, { current: number; required: number }>;
}

export const BadgeGrid = ({ badges, earnedBadges, progressMap = {} }: BadgeGridProps) => {
  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
      {badges.map((badge) => (
        <BadgeCard
          key={badge.id}
          badge={badge}
          isEarned={earnedBadges.some((b) => b.id === badge.id)}
          progress={progressMap[badge.id]}
        />
      ))}
    </div>
  );
}; 