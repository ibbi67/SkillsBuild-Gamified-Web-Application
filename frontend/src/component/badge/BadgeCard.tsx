import { Badge } from '@/types/databaseTypes';
import Image from 'next/image';
import { useState } from 'react';

interface BadgeCardProps {
  badge: Badge;
  isEarned?: boolean;
  progress?: {
    current: number;
    required: number;
  };
}

export const BadgeCard = ({ badge, isEarned = false, progress }: BadgeCardProps) => {
  const [imageError, setImageError] = useState(false);

  return (
    <div className={`relative rounded-lg overflow-hidden shadow-md transition-transform hover:scale-105 ${!isEarned ? 'opacity-50' : ''}`}>
      <div className="relative h-48 w-48">
        {imageError ? (
          <div className="flex h-full w-full items-center justify-center bg-gray-100">
            <div className="text-center">
              <div className="text-4xl mb-2">🏆</div>
              <div className="text-sm text-gray-500">{badge.name}</div>
            </div>
          </div>
        ) : (
          <Image
            src={badge.imageUrl}
            alt={badge.name}
            fill
            className="object-contain"
            onError={() => setImageError(true)}
          />
        )}
      </div>
      <div className="p-4 bg-white">
        <h3 className="text-lg font-semibold text-gray-800">{badge.name}</h3>
        <p className="text-sm text-gray-600 mt-1">{badge.description}</p>
        {progress && (
          <div className="mt-2">
            <div className="w-full bg-gray-200 rounded-full h-2.5">
              <div
                className="bg-blue-600 h-2.5 rounded-full"
                style={{ width: `${(progress.current / progress.required) * 100}%` }}
              ></div>
            </div>
            <p className="text-xs text-gray-500 mt-1">
              {progress.current}/{progress.required}
            </p>
          </div>
        )}
      </div>
    </div>
  );
}; 