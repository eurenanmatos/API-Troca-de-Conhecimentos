import React from 'react';
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend } from 'chart.js';
import { Line } from 'react-chartjs-2';

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend);

interface ChartProps {
    data: {
        labels: string[];
        datasets: {
            label: string;
            data: number[];
            borderColor: string;
            backgroundColor: string;
        }[];
    };
    options?: {
        responsive?: boolean;
        plugins?: {
            legend?: {
                display?: boolean;
            };
        };
    };
}

const Chart: React.FC<ChartProps> = ({ data, options }) => {
    return <Line data={data} options={options} />;
};

export default Chart;