"use client";

import Link from "next/link";
import Logo from "./Logo";
import { useAuth } from "@/providers/AuthProvider";
import { useApi } from "@/hooks/useApi";
import { LogoutRequest, LogoutResponse } from "@/types/apiCall";
import { useRouter, usePathname } from "next/navigation";

export default function Navbar() {
    const pathname = usePathname();
    const router = useRouter();
    const { isAuthenticated, setIsAuthenticated } = useAuth();
    const { status, fetchData } = useApi<LogoutResponse, LogoutRequest>(
        "auth/logout",
        {
            method: "POST",
        }
    );

    const logoutOnClick = async () => {
        await fetchData();
        setIsAuthenticated(status !== 200);
        router.push("/");
    };

    const getLinkClass = (path: string) => {
        return pathname === path
            ? "rounded px-4 py-2 text-white bg-blue-500"
            : "rounded px-4 py-2 text-blue-500";
    };

    return (
        <nav className="grid grow grid-cols-3 items-center rounded-2xl bg-white px-8 py-4 shadow-lg">
            <div className="flex items-center">
                <Logo />
            </div>
            <div className="flex justify-center gap-2">
                <Link className={getLinkClass("/dashboard")} href="/dashboard">
                    Dashboard
                </Link>
                <Link
                    className={getLinkClass("/leaderboard")}
                    href="/leaderboard"
                >
                    Leaderboard
                </Link>
                <Link className={getLinkClass("/course")} href="/course">
                    Course
                </Link>
            </div>
            <div className="flex justify-end gap-2">
                {isAuthenticated ? (
                    <>
                        <Link
                            className={getLinkClass("/profile")}
                            href="/profile"
                        >
                            Profile
                        </Link>
                        <button
                            className="rounded px-4 py-2 text-red-500"
                            onClick={logoutOnClick}
                        >
                            Logout
                        </button>
                    </>
                ) : (
                    <>
                        <Link
                            className={getLinkClass("/auth/login")}
                            href="/auth/login"
                        >
                            Login
                        </Link>
                        <Link
                            className={getLinkClass("/auth/signup")}
                            href="/auth/signup"
                        >
                            Sign Up!
                        </Link>
                    </>
                )}
            </div>
        </nav>
    );
}
