interface TopCardProps {
    rank: number;
    name: string;
    score: number;
}

const rankColors = ["bg-gold", "bg-silver", "bg-bronze"];

export default function TopCard({ rank, name, score }: TopCardProps) {
    return (
        <div
            className={
                `${rankColors[rank - 1]} ` +
                "flex grow flex-col items-center rounded-lg p-4 shadow-lg"
            }
        >
            <div className="text-2xl font-bold">{rank}</div>
            <div className="text-xl">{name}</div>
            <div className="text-lg">{score} points</div>
        </div>
    );
}
