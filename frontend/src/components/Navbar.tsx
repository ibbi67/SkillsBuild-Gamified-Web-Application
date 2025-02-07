"use client";

import Link from "next/link";
import Logo from "./Logo";
import { useRouter } from "next/navigation";

export default function Navbar() {
    const router = useRouter();
    const token = localStorage.getItem("login");

    const logoutOnClick = () => {
        localStorage.removeItem("login");
        router.refresh();
    };

    return (
        <nav className="grid grid-cols-3 items-center py-4 px-8 rounded-2xl bg-white grow shadow-lg">
            <div className="flex items-center">
                <Logo />
            </div>
            <div>{/* This is where the links to other places will go */}</div>
            <div className="flex justify-end gap-2">
                {token ? (
                    <>
                        <Link className="py-2 bg-blue-500 text-white rounded px-4" href="/profile">
                            Profile
                        </Link>
                        <button className="py-2 text-red-500 rounded px-4" onClick={logoutOnClick}>
                            Logout
                        </button>
                    </>
                ) : (
                    <>
                        <Link className="py-2 text-blue-500 rounded px-4" href="/auth/login">
                            Login
                        </Link>
                        <Link
                            className="bg-blue-500 py-2 text-white rounded px-4"
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
