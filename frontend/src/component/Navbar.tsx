"use client";

import Link from "next/link";
import Logo from "./Logo";
import { usePathname } from "next/navigation";
import { Person } from "@/types/databaseTypes";

interface NavbarProps {
    user: Person | undefined;
    logoutOnClick: () => void;
}

export default function Navbar({ user, logoutOnClick }: NavbarProps) {
    const pathname = usePathname();

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
                <Link className={getLinkClass("/leaderboard")} href="/leaderboard">
                    Leaderboard
                </Link>
                <Link className={getLinkClass("/course")} href="/course">
                    Course
                </Link>
            </div>
            <div className="flex justify-end gap-2">
                {user ? (
                    <>
                        <Link className={getLinkClass("/dashboard")} href="/dashboard">
                            Dashboard
                        </Link>
                        <button className="rounded px-4 py-2 text-red-500" onClick={logoutOnClick}>
                            Logout
                        </button>
                        {user.avatarLink && <img src={user.avatarLink} alt="User Avatar" className="w-8 h-8 rounded-full" />}
                    </>
                ) : (
                    <>
                        <Link className={getLinkClass("/auth/login")} href="/auth/login">
                            Login
                        </Link>
                        <Link className={getLinkClass("/auth/signup")} href="/auth/signup">
                            Sign Up!
                        </Link>
                    </>
                )}
            </div>
        </nav>
    );
}
