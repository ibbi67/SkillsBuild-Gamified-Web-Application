import { useState } from "react";
import { useAddFavourite, useRemoveFavourite, useFavourites } from "@/queries";
import { useQueryClient } from "@tanstack/react-query";
import { toast } from "react-hot-toast";
import { Heart, Loader2 } from "lucide-react";

interface FavoriteButtonProps {
    courseId: number;
    disabled: boolean;
}

export function FavoriteButton({ courseId, disabled }: FavoriteButtonProps) {
    const [isLoading, setIsLoading] = useState(false);
    const queryClient = useQueryClient();
    const { data: favorites } = useFavourites();
    const addFavoriteMutation = useAddFavourite();
    const removeFavoriteMutation = useRemoveFavourite();

    const isFavorited = favorites?.data.some(
        (favorite) => favorite.id === courseId
    );

    const handleFavorite = async () => {
        if (isLoading) return;
        
        setIsLoading(true);
        try {
            if (isFavorited) {
                await removeFavoriteMutation.mutateAsync({ courseId });
                toast.success("Removed from favorites");
            } else {
                await addFavoriteMutation.mutateAsync({ courseId });
                toast.success("Added to favorites");
            }
            await queryClient.invalidateQueries({ queryKey: ["favourites"] });
        } catch (error) {
            toast.error(error instanceof Error ? error.message : "Failed to update favorites");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <button
            onClick={handleFavorite}
            disabled={disabled || isLoading}
            className={`p-2 rounded-lg transition-colors
                ${isLoading ? "bg-gray-100" : "hover:bg-gray-100"}
                ${disabled ? "opacity-50 cursor-not-allowed" : ""}`}
            aria-label={isFavorited ? "Remove from favorites" : "Add to favorites"}
        >
            {isLoading ? (
                <Loader2 className="h-6 w-6 text-gray-500 animate-spin" />
            ) : isFavorited ? (
                <Heart className="h-6 w-6 text-red-500 fill-current" />
            ) : (
                <Heart className="h-6 w-6 text-gray-500" />
            )}
        </button>
    );
}