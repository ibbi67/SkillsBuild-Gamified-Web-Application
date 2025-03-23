import { useState, useEffect } from "react";
import { Course } from "@/types/databaseTypes";
import { Heart } from "lucide-react";
import { useAddFavourite } from "@/queries/favourites/useAddFavourite";
import { useRemoveFavourite } from "@/queries/favourites/useRemoveFavourite";
import { useFavourites } from "@/queries/favourites/useFavourites";
import { useQueryClient } from '@tanstack/react-query';
import toast from "react-hot-toast";
import { useCreateEnrollment } from "@/queries/enrollments/useCreateEnrollment";

interface CourseCardProps {
    course: Course;
}

export default function CourseCard({ course }: CourseCardProps) {
    const [isFavourite, setIsFavourite] = useState(false);
    const [isExpanded, setIsExpanded] = useState(false);
    const queryClient = useQueryClient();

    const { data: favourites } = useFavourites();
    const { mutate: addFavourite, isPending: isAddingFavourite, isError: isAddFavouriteError, error: addError } = useAddFavourite();
    const { mutate: removeFavourite, isPending: isRemovingFavourite, isError: isRemoveFavouriteError, error: removeError } = useRemoveFavourite();
    const { mutate: enroll, isPending: isEnrolling, isError: isEnrollError, error: enrollError } = useCreateEnrollment();

    useEffect(() => {
        if (favourites) {
            setIsFavourite(favourites?.data.some((fav) => fav.id === course.id));
        }
    }, [favourites, course.id]);

    useEffect(() => {
        if (isAddFavouriteError) {
            toast.error("Error adding to favourites: " + addError.message);
        }
        if (isRemoveFavouriteError) {
            toast.error("Error removing from favourites: " + removeError.message);
        }
        if (isEnrollError) {
            toast.error("Error enrolling: " + enrollError.message);
        }
    }, [isAddFavouriteError, isRemoveFavouriteError, addError, removeError, isEnrollError, enrollError]);

    const toggleFavourite = () => {
        if (isFavourite) {
            removeFavourite({ courseId: course.id }, {
                onSuccess: () => {
                    queryClient.invalidateQueries({ queryKey: ['favourites'] });
                },
            });
        } else {
            addFavourite({ courseId: course.id }, {
                onSuccess: () => {
                    queryClient.invalidateQueries({ queryKey: ['favourites'] });
                },
            });
        }
        setIsFavourite(!isFavourite);
    };

    const openCourseLink = () => {
        window.open(course.link, "_blank", "noopener,noreferrer");
    };

    const handleEnroll = () => {
        enroll({ courseId: course.id }, {
            onSuccess: () => {
                toast.success("Enrolled successfully!");
            },
            onError: (error) => {
                toast.error(`Error enrolling: ${error.message}`);
            },
        });
    };

    return (
        <div className="flex cursor-pointer flex-col overflow-hidden rounded-lg border border-blue-500 shadow-lg transition hover:border-blue-600 hover:bg-blue-50">
            <h3 className="bg-blue-500 p-4 text-lg font-bold text-white">
                {course.title}
            </h3>

            {(() => {
                const description = isExpanded
                    ? course.description
                    : course.description.split("\n").slice(0, 3).join("\n");
                return (
                    <div
                        dangerouslySetInnerHTML={{
                            __html: description.replace(/\n/g, "<br />"),
                        }}
                        className={
                            "m-4 text-sm " +
                            (isExpanded ? "line-clamp-none" : "line-clamp-3")
                        }
                    />
                );
            })()}

            <button
                onClick={() => { setIsExpanded(!isExpanded) }}
                className="mr-4 self-end text-blue-500 hover:underline"
            >
                {isExpanded ? "Read Less" : "Read More"}
            </button>

            <div className="align-center flex gap-4 p-4 text-white">
                <button
                    onClick={openCourseLink}
                    className="rounded bg-blue-500 px-4 py-2 cursor-pointer"
                >
                    Go to Course
                </button>

                <button
                    onClick={handleEnroll}
                    className="rounded bg-green-500 px-4 py-2 cursor-pointer"
                    disabled={isEnrolling}
                >
                    {isEnrolling ? "Enrolling..." : "Enroll"}
                </button>

                <button
                    onClick={toggleFavourite}
                    className="align-center flex items-center gap-2 rounded px-4 py-2 text-blue-500 outline outline-blue-500 cursor-pointer"
                    disabled={isAddingFavourite || isRemovingFavourite}
                >
                    {isFavourite ? "Remove from Favorites" : "Add to Favorites"}
                    <Heart
                        className={`h-5 w-5 transition ${isFavourite
                            ? "fill-red-500 text-red-500"
                            : "text-blue-500"
                        }`}
                    />
                </button>
            </div>
        </div>
    );
}
