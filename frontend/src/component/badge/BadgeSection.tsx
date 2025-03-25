import { useGetUserBadges } from "@/queries/badges/useGetUserBadges";
import { BadgeCard } from "./BadgeCard";
import { useMe } from "@/queries/auth/useMe";
import Link from "next/link";
import { Badge } from "@/types/databaseTypes";

export const BadgeSection = () => {
    const { data: user } = useMe();
    const { data: userBadges, isLoading } = useGetUserBadges(user?.data?.id ?? 0);

    if (isLoading) {
        return <div>Loading badges...</div>;
    }

    if (!userBadges || userBadges.length === 0) {
        return (
            <div className="rounded-lg border p-4 shadow">
                <h2 className="mb-2 font-bold">Your Badges</h2>
                <p>No badges earned yet. Keep learning to earn badges!</p>
            </div>
        );
    }

    return (
        <div className="rounded-lg border p-4 shadow">
            <div className="mb-4 flex items-center justify-between">
                <h2 className="font-bold">Your Badges</h2>
                <Link href="/badges" className="text-sm text-blue-500 hover:underline">
                    View All
                </Link>
            </div>
            <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4">
                {userBadges.slice(0, 4).map((badge: Badge) => (
                    <BadgeCard key={badge.id} badge={badge} isEarned={true} />
                ))}
            </div>
        </div>
    );
}; 