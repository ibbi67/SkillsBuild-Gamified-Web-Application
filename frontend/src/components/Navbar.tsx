"use client";

import Link from "next/link";
import Logo from "./Logo";
import { useAuth } from "@/providers/AuthProvider";
import { useApi } from "@/hooks/useApi";
import { LogoutRequest, LogoutResponse } from "@/types/auth";
import { useRouter } from "next/navigation";

export default function Navbar() {
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

    return (
        <nav className="grid grow grid-cols-3 items-center rounded-2xl bg-white px-8 py-4 shadow-lg">
            <div className="flex items-center">
                <Logo />
            </div>
            <div>{/* This is where the links to other places will go */}</div>
            <div className="flex justify-end gap-2">
                {isAuthenticated ? (
                    <>
                        <Link
                            className="rounded bg-blue-500 px-4 py-2 text-white"
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
                            className="rounded px-4 py-2 text-blue-500"
                            href="/auth/login"
                        >
                            Login
                        </Link>
                        <Link
                            className="rounded bg-blue-500 px-4 py-2 text-white"
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
