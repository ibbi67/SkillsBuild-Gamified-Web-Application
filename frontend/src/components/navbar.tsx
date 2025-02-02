import Link from "next/link";
import Image from "next/image";

export default function NavBar() {
    return (
        <nav className="grid grid-cols-3 items-center py-4 px-8 rounded-2xl bg-white grow shadow-lg">
            <div className="flex items-center">
                <Image src="logo.svg" height="50" width="50" alt="logo" />
                <p className="invisible md:visible font-bold text-xl text-blue-500">SkillShare++</p>
            </div>
            <div>{/* This is where the links to other places will go */}</div>
            <div className="flex justify-end">
                <Link className="bg-blue-500 py-2 text-white rounded px-4" href="/auth/signup">
                    Sign Up!
                </Link>
            </div>
        </nav>
    );
}
