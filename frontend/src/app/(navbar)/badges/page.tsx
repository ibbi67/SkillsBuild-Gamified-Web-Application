"use client";

import { useGetAllBadges } from "@/queries/badges/useGetAllBadges";
import { useGetUserBadges } from "@/queries/badges/useGetUserBadges";
import { useMe } from "@/queries/auth/useMe";
import { BadgeGrid } from "@/component/badge/BadgeGrid";
import toast from "react-hot-toast";

export default function BadgesPage() {
    const { data: user } = useMe();
    const { data: allBadges, isLoading: isLoadingAllBadges } = useGetAllBadges();
    const { data: userBadges, isLoading: isLoadingUserBadges } = useGetUserBadges(user?.data?.id ?? 0);

    if (isLoadingAllBadges || isLoadingUserBadges) {
        return <div>Loading badges...</div>;
    }

    if (!allBadges) {
        toast.error("Failed to load badges");
        return <div>Error loading badges</div>;
    }

    return (
        <div className="m-4 mx-auto w-4/5 rounded-lg bg-white p-4 shadow-lg">
            <h1 className="mb-6 text-2xl font-bold">All Badges</h1>
            <BadgeGrid 
                badges={allBadges} 
                earnedBadges={userBadges ?? []}
            />
        </div>
    );
} 