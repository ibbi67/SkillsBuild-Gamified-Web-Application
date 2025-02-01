export default function SignUpPage() {
    return (
        <div className="flex justify-center items-center h-screen">
            <div className="flex flex-col p-4 bg-white rounded-lg shadow-lg gap-4 grow max-w-96">
                <h1 className="font-bold text-center md:text-2xl">Sign Up</h1>
                <input
                    type="text"
                    placeholder="Username"
                    className="py-1 px-2 md:py-2 border border-gray-300 rounded-lg"
                />
                <input
                    type="password"
                    placeholder="Password"
                    className="py-1 px-2 md:py-2 border border-gray-300 rounded-lg"
                />
                <button className="bg-blue-500 text-white p-2 rounded-lg">Confirm</button>
            </div>
        </div>
    );
}
