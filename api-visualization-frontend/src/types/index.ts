export interface ApiResponse {
    data: any;
    status: number;
    message: string;
}

export interface ChartData {
    labels: string[];
    datasets: {
        label: string;
        data: number[];
        backgroundColor: string[];
    }[];
}