import { useState } from 'react';
import { 
  TrendingUp, TrendingDown, Minus, Download, Package, Database, 
  Star, Eye, Users, ArrowUpRight, ArrowDownRight, Filter, Search,
  Crown, Flame, Zap, Calendar, BarChart3, ChevronLeft, ChevronRight
} from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Input } from '@/components/ui/input';
import { useLanguage } from '@/components/LanguageProvider';
import { LineChart, Line, BarChart, Bar, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts';

export function Trending() {
  const { t } = useLanguage();
  const [timeRange, setTimeRange] = useState('thisWeek');
  const [formatFilter, setFormatFilter] = useState('all');
  const [searchQuery, setSearchQuery] = useState('');
  const [artifactsPage, setArtifactsPage] = useState(1);
  const [repositoriesPage, setRepositoriesPage] = useState(1);
  const itemsPerPage = 10;

  // Mock data for trending artifacts
  const trendingArtifacts = [
    {
      id: '1',
      rank: 1,
      name: 'spring-boot-starter-web',
      groupId: 'org.springframework.boot',
      version: '3.2.0',
      repository: 'maven-central',
      format: 'Maven',
      downloads: 45632,
      weeklyDownloads: 8234,
      growth: 23.5,
      trend: 'up',
      uniqueUsers: 1234,
      stars: 892,
      lastUpdated: '2 days ago',
    },
    {
      id: '2',
      rank: 2,
      name: 'nginx',
      groupId: 'library',
      version: 'latest',
      repository: 'docker-hub-proxy',
      format: 'Docker',
      downloads: 38945,
      weeklyDownloads: 7123,
      growth: 18.2,
      trend: 'up',
      uniqueUsers: 956,
      stars: 2134,
      lastUpdated: '1 day ago',
    },
    {
      id: '3',
      rank: 3,
      name: 'react',
      groupId: 'facebook',
      version: '18.2.0',
      repository: 'npm-public',
      format: 'NPM',
      downloads: 34521,
      weeklyDownloads: 6789,
      growth: 15.8,
      trend: 'up',
      uniqueUsers: 2345,
      stars: 3421,
      lastUpdated: '3 days ago',
    },
    {
      id: '4',
      rank: 4,
      name: 'jackson-databind',
      groupId: 'com.fasterxml.jackson.core',
      version: '2.16.0',
      repository: 'maven-central',
      format: 'Maven',
      downloads: 29834,
      weeklyDownloads: 5234,
      growth: 12.3,
      trend: 'up',
      uniqueUsers: 789,
      stars: 567,
      lastUpdated: '1 week ago',
    },
    {
      id: '5',
      rank: 5,
      name: 'postgres',
      groupId: 'library',
      version: '16.1',
      repository: 'docker-hub-proxy',
      format: 'Docker',
      downloads: 27456,
      weeklyDownloads: 4923,
      growth: 9.8,
      trend: 'up',
      uniqueUsers: 645,
      stars: 1234,
      lastUpdated: '5 days ago',
    },
    {
      id: '6',
      rank: 6,
      name: 'lodash',
      groupId: 'utilities',
      version: '4.17.21',
      repository: 'npm-public',
      format: 'NPM',
      downloads: 25123,
      weeklyDownloads: 4567,
      growth: 7.2,
      trend: 'stable',
      uniqueUsers: 1876,
      stars: 987,
      lastUpdated: '2 weeks ago',
    },
    {
      id: '7',
      rank: 7,
      name: 'guava',
      groupId: 'com.google.guava',
      version: '32.1.3',
      repository: 'maven-central',
      format: 'Maven',
      downloads: 23489,
      weeklyDownloads: 4234,
      growth: 5.4,
      trend: 'stable',
      uniqueUsers: 543,
      stars: 456,
      lastUpdated: '4 days ago',
    },
    {
      id: '8',
      rank: 8,
      name: 'redis',
      groupId: 'library',
      version: '7.2',
      repository: 'docker-hub-proxy',
      format: 'Docker',
      downloads: 21345,
      weeklyDownloads: 3987,
      growth: 3.2,
      trend: 'stable',
      uniqueUsers: 432,
      stars: 876,
      lastUpdated: '6 days ago',
    },
    {
      id: '9',
      rank: 9,
      name: 'axios',
      groupId: 'http-client',
      version: '1.6.2',
      repository: 'npm-public',
      format: 'NPM',
      downloads: 19876,
      weeklyDownloads: 3654,
      growth: -2.1,
      trend: 'down',
      uniqueUsers: 1543,
      stars: 765,
      lastUpdated: '1 week ago',
    },
    {
      id: '10',
      rank: 10,
      name: 'lombok',
      groupId: 'org.projectlombok',
      version: '1.18.30',
      repository: 'maven-central',
      format: 'Maven',
      downloads: 18234,
      weeklyDownloads: 3345,
      growth: -4.5,
      trend: 'down',
      uniqueUsers: 456,
      stars: 234,
      lastUpdated: '3 days ago',
    },
    {
      id: '11',
      rank: 11,
      name: 'mysql',
      groupId: 'library',
      version: '8.2',
      repository: 'docker-hub-proxy',
      format: 'Docker',
      downloads: 17856,
      weeklyDownloads: 3123,
      growth: 6.8,
      trend: 'up',
      uniqueUsers: 389,
      stars: 654,
      lastUpdated: '2 days ago',
    },
    {
      id: '12',
      rank: 12,
      name: 'vue',
      groupId: 'vuejs',
      version: '3.4.0',
      repository: 'npm-public',
      format: 'NPM',
      downloads: 16789,
      weeklyDownloads: 2987,
      growth: 8.3,
      trend: 'up',
      uniqueUsers: 1678,
      stars: 2134,
      lastUpdated: '4 days ago',
    },
    {
      id: '13',
      rank: 13,
      name: 'slf4j-api',
      groupId: 'org.slf4j',
      version: '2.0.9',
      repository: 'maven-central',
      format: 'Maven',
      downloads: 15234,
      weeklyDownloads: 2765,
      growth: 4.2,
      trend: 'stable',
      uniqueUsers: 423,
      stars: 321,
      lastUpdated: '1 week ago',
    },
    {
      id: '14',
      rank: 14,
      name: 'express',
      groupId: 'web-framework',
      version: '4.18.2',
      repository: 'npm-public',
      format: 'NPM',
      downloads: 14567,
      weeklyDownloads: 2543,
      growth: 3.7,
      trend: 'stable',
      uniqueUsers: 1234,
      stars: 876,
      lastUpdated: '5 days ago',
    },
    {
      id: '15',
      rank: 15,
      name: 'mongodb',
      groupId: 'library',
      version: '7.0',
      repository: 'docker-hub-proxy',
      format: 'Docker',
      downloads: 13892,
      weeklyDownloads: 2398,
      growth: 5.1,
      trend: 'stable',
      uniqueUsers: 312,
      stars: 543,
      lastUpdated: '3 days ago',
    },
    {
      id: '16',
      rank: 16,
      name: 'junit-jupiter',
      groupId: 'org.junit.jupiter',
      version: '5.10.1',
      repository: 'maven-central',
      format: 'Maven',
      downloads: 12456,
      weeklyDownloads: 2187,
      growth: 2.8,
      trend: 'stable',
      uniqueUsers: 378,
      stars: 289,
      lastUpdated: '6 days ago',
    },
    {
      id: '17',
      rank: 17,
      name: 'typescript',
      groupId: 'microsoft',
      version: '5.3.3',
      repository: 'npm-public',
      format: 'NPM',
      downloads: 11789,
      weeklyDownloads: 2054,
      growth: 1.9,
      trend: 'stable',
      uniqueUsers: 1567,
      stars: 1987,
      lastUpdated: '2 days ago',
    },
    {
      id: '18',
      rank: 18,
      name: 'rabbitmq',
      groupId: 'library',
      version: '3.12',
      repository: 'docker-hub-proxy',
      format: 'Docker',
      downloads: 10923,
      weeklyDownloads: 1923,
      growth: 0.5,
      trend: 'stable',
      uniqueUsers: 245,
      stars: 432,
      lastUpdated: '1 week ago',
    },
    {
      id: '19',
      rank: 19,
      name: 'webpack',
      groupId: 'bundler',
      version: '5.89.0',
      repository: 'npm-public',
      format: 'NPM',
      downloads: 10234,
      weeklyDownloads: 1789,
      growth: -1.3,
      trend: 'down',
      uniqueUsers: 987,
      stars: 654,
      lastUpdated: '4 days ago',
    },
    {
      id: '20',
      rank: 20,
      name: 'gson',
      groupId: 'com.google.code.gson',
      version: '2.10.1',
      repository: 'maven-central',
      format: 'Maven',
      downloads: 9876,
      weeklyDownloads: 1654,
      growth: -2.7,
      trend: 'down',
      uniqueUsers: 312,
      stars: 234,
      lastUpdated: '5 days ago',
    },
    {
      id: '21',
      rank: 21,
      name: 'elasticsearch',
      groupId: 'library',
      version: '8.11',
      repository: 'docker-hub-proxy',
      format: 'Docker',
      downloads: 9234,
      weeklyDownloads: 1543,
      growth: 4.6,
      trend: 'stable',
      uniqueUsers: 198,
      stars: 345,
      lastUpdated: '3 days ago',
    },
    {
      id: '22',
      rank: 22,
      name: 'moment',
      groupId: 'date-time',
      version: '2.29.4',
      repository: 'npm-public',
      format: 'NPM',
      downloads: 8765,
      weeklyDownloads: 1432,
      growth: -3.8,
      trend: 'down',
      uniqueUsers: 765,
      stars: 432,
      lastUpdated: '2 weeks ago',
    },
    {
      id: '23',
      rank: 23,
      name: 'logback-classic',
      groupId: 'ch.qos.logback',
      version: '1.4.14',
      repository: 'maven-central',
      format: 'Maven',
      downloads: 8234,
      weeklyDownloads: 1321,
      growth: 1.2,
      trend: 'stable',
      uniqueUsers: 289,
      stars: 187,
      lastUpdated: '1 week ago',
    },
    {
      id: '24',
      rank: 24,
      name: 'tailwindcss',
      groupId: 'css-framework',
      version: '3.4.0',
      repository: 'npm-public',
      format: 'NPM',
      downloads: 7892,
      weeklyDownloads: 1265,
      growth: 11.4,
      trend: 'up',
      uniqueUsers: 1123,
      stars: 1456,
      lastUpdated: '2 days ago',
    },
    {
      id: '25',
      rank: 25,
      name: 'kafka',
      groupId: 'library',
      version: '3.6',
      repository: 'docker-hub-proxy',
      format: 'Docker',
      downloads: 7456,
      weeklyDownloads: 1187,
      growth: 7.9,
      trend: 'up',
      uniqueUsers: 167,
      stars: 289,
      lastUpdated: '4 days ago',
    },
  ];

  // Mock data for trending repositories
  const trendingRepositories = [
    {
      id: '1',
      rank: 1,
      name: 'maven-central',
      type: 'proxy',
      format: 'Maven',
      artifacts: 1245,
      downloads: 156789,
      growth: 28.5,
      trend: 'up',
      storage: '45.2 GB',
      activeUsers: 234,
    },
    {
      id: '2',
      rank: 2,
      name: 'docker-hub-proxy',
      type: 'proxy',
      format: 'Docker',
      artifacts: 892,
      downloads: 134567,
      growth: 22.3,
      trend: 'up',
      storage: '128.5 GB',
      activeUsers: 189,
    },
    {
      id: '3',
      rank: 3,
      name: 'npm-public',
      type: 'group',
      format: 'NPM',
      artifacts: 2345,
      downloads: 98765,
      growth: 18.7,
      trend: 'up',
      storage: '23.4 GB',
      activeUsers: 345,
    },
    {
      id: '4',
      rank: 4,
      name: 'nuget-gallery',
      type: 'proxy',
      format: 'NuGet',
      artifacts: 567,
      downloads: 67543,
      growth: 12.4,
      trend: 'up',
      storage: '15.8 GB',
      activeUsers: 98,
    },
    {
      id: '5',
      rank: 5,
      name: 'pypi-proxy',
      type: 'proxy',
      format: 'PyPI',
      artifacts: 1123,
      downloads: 54321,
      growth: 8.9,
      trend: 'stable',
      storage: '34.7 GB',
      activeUsers: 156,
    },
    {
      id: '6',
      rank: 6,
      name: 'apt-releases',
      type: 'hosted',
      format: 'APT',
      artifacts: 432,
      downloads: 45678,
      growth: 6.5,
      trend: 'stable',
      storage: '18.9 GB',
      activeUsers: 87,
    },
    {
      id: '7',
      rank: 7,
      name: 'yum-snapshots',
      type: 'hosted',
      format: 'YUM',
      artifacts: 389,
      downloads: 38234,
      growth: 5.2,
      trend: 'stable',
      storage: '21.3 GB',
      activeUsers: 76,
    },
    {
      id: '8',
      rank: 8,
      name: 'helm-charts',
      type: 'proxy',
      format: 'Helm',
      artifacts: 278,
      downloads: 32156,
      growth: 9.7,
      trend: 'up',
      storage: '5.6 GB',
      activeUsers: 124,
    },
    {
      id: '9',
      rank: 9,
      name: 'go-modules',
      type: 'proxy',
      format: 'Go',
      artifacts: 654,
      downloads: 28934,
      growth: 11.3,
      trend: 'up',
      storage: '12.4 GB',
      activeUsers: 145,
    },
    {
      id: '10',
      rank: 10,
      name: 'raw-releases',
      type: 'hosted',
      format: 'Raw',
      artifacts: 198,
      downloads: 24567,
      growth: 3.8,
      trend: 'stable',
      storage: '8.7 GB',
      activeUsers: 56,
    },
    {
      id: '11',
      rank: 11,
      name: 'docker-private',
      type: 'hosted',
      format: 'Docker',
      artifacts: 234,
      downloads: 21345,
      growth: 7.4,
      trend: 'up',
      storage: '45.6 GB',
      activeUsers: 98,
    },
    {
      id: '12',
      rank: 12,
      name: 'npm-internal',
      type: 'hosted',
      format: 'NPM',
      artifacts: 567,
      downloads: 19876,
      growth: 4.9,
      trend: 'stable',
      storage: '6.8 GB',
      activeUsers: 112,
    },
    {
      id: '13',
      rank: 13,
      name: 'maven-snapshots',
      type: 'hosted',
      format: 'Maven',
      artifacts: 432,
      downloads: 18234,
      growth: 2.7,
      trend: 'stable',
      storage: '19.3 GB',
      activeUsers: 87,
    },
    {
      id: '14',
      rank: 14,
      name: 'pypi-internal',
      type: 'hosted',
      format: 'PyPI',
      artifacts: 189,
      downloads: 16543,
      growth: 5.6,
      trend: 'stable',
      storage: '7.2 GB',
      activeUsers: 54,
    },
    {
      id: '15',
      rank: 15,
      name: 'nuget-internal',
      type: 'hosted',
      format: 'NuGet',
      artifacts: 145,
      downloads: 14789,
      growth: 3.2,
      trend: 'stable',
      storage: '4.5 GB',
      activeUsers: 43,
    },
  ];

  // Chart data
  const downloadTrendData = [
    { date: '01-13', downloads: 28000 },
    { date: '01-14', downloads: 32000 },
    { date: '01-15', downloads: 35000 },
    { date: '01-16', downloads: 38000 },
    { date: '01-17', downloads: 42000 },
    { date: '01-18', downloads: 45000 },
    { date: '01-19', downloads: 48000 },
  ];

  const categoryDistributionData = [
    { name: 'Maven', value: 35, color: '#f97316' },
    { name: 'Docker', value: 28, color: '#3b82f6' },
    { name: 'NPM', value: 22, color: '#ef4444' },
    { name: 'NuGet', value: 10, color: '#8b5cf6' },
    { name: 'PyPI', value: 5, color: '#eab308' },
  ];

  const growthRateData = [
    { name: 'Spring Boot', growth: 23.5 },
    { name: 'Nginx', growth: 18.2 },
    { name: 'React', growth: 15.8 },
    { name: 'Jackson', growth: 12.3 },
    { name: 'Postgres', growth: 9.8 },
    { name: 'Lodash', growth: 7.2 },
  ];

  const getTrendIcon = (trend: string) => {
    if (trend === 'up') return <TrendingUp className="size-4 text-green-500" />;
    if (trend === 'down') return <TrendingDown className="size-4 text-red-500" />;
    return <Minus className="size-4 text-gray-400" />;
  };

  const getTrendBadge = (growth: number) => {
    if (growth > 10) return <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300">+{growth}%</Badge>;
    if (growth > 0) return <Badge className="bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300">+{growth}%</Badge>;
    return <Badge className="bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-300">{growth}%</Badge>;
  };

  const getFormatColor = (format: string) => {
    const colors: Record<string, string> = {
      'Maven': 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-300',
      'Docker': 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300',
      'NPM': 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-300',
      'NuGet': 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-300',
      'PyPI': 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-300',
    };
    return colors[format] || 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-300';
  };

  const getRankBadge = (rank: number) => {
    if (rank === 1) return <Crown className="size-5 text-yellow-500" />;
    if (rank === 2) return <Crown className="size-5 text-gray-400" />;
    if (rank === 3) return <Crown className="size-5 text-orange-400" />;
    return <span className="text-sm text-gray-500 dark:text-gray-400">#{rank}</span>;
  };

  const filteredArtifacts = trendingArtifacts.filter(artifact => {
    const matchesSearch = artifact.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         artifact.groupId.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesFormat = formatFilter === 'all' || artifact.format === formatFilter;
    return matchesSearch && matchesFormat;
  });

  const paginatedArtifacts = filteredArtifacts.slice((artifactsPage - 1) * itemsPerPage, artifactsPage * itemsPerPage);
  const paginatedRepositories = trendingRepositories.slice((repositoriesPage - 1) * itemsPerPage, repositoriesPage * itemsPerPage);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl text-gray-900 dark:text-white">{t('trending.pageTitle')}</h1>
        <p className="text-gray-600 dark:text-gray-400 mt-1">{t('trending.pageSubtitle')}</p>
      </div>

      {/* Stats Overview */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">{t('trending.totalDownloadsToday')}</p>
                <p className="text-2xl font-semibold text-gray-900 dark:text-white mt-1">48,234</p>
                <div className="flex items-center gap-1 mt-2">
                  <ArrowUpRight className="size-4 text-green-500" />
                  <span className="text-sm text-green-600 dark:text-green-400">+12.5%</span>
                  <span className="text-xs text-gray-500 dark:text-gray-400">{t('analytics.vsLastWeek')}</span>
                </div>
              </div>
              <div className="size-12 bg-blue-100 dark:bg-blue-900/30 rounded-lg flex items-center justify-center">
                <Download className="size-6 text-blue-600 dark:text-blue-400" />
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">{t('trending.totalArtifacts')}</p>
                <p className="text-2xl font-semibold text-gray-900 dark:text-white mt-1">5,847</p>
                <div className="flex items-center gap-1 mt-2">
                  <ArrowUpRight className="size-4 text-green-500" />
                  <span className="text-sm text-green-600 dark:text-green-400">+234</span>
                  <span className="text-xs text-gray-500 dark:text-gray-400">{t('trending.thisWeek')}</span>
                </div>
              </div>
              <div className="size-12 bg-green-100 dark:bg-green-900/30 rounded-lg flex items-center justify-center">
                <Package className="size-6 text-green-600 dark:text-green-400" />
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">{t('trending.totalRepositories')}</p>
                <p className="text-2xl font-semibold text-gray-900 dark:text-white mt-1">18</p>
                <div className="flex items-center gap-1 mt-2">
                  <ArrowUpRight className="size-4 text-green-500" />
                  <span className="text-sm text-green-600 dark:text-green-400">+2</span>
                  <span className="text-xs text-gray-500 dark:text-gray-400">{t('trending.thisMonth')}</span>
                </div>
              </div>
              <div className="size-12 bg-purple-100 dark:bg-purple-900/30 rounded-lg flex items-center justify-center">
                <Database className="size-6 text-purple-600 dark:text-purple-400" />
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">{t('trending.activeUsers')}</p>
                <p className="text-2xl font-semibold text-gray-900 dark:text-white mt-1">1,234</p>
                <div className="flex items-center gap-1 mt-2">
                  <ArrowUpRight className="size-4 text-green-500" />
                  <span className="text-sm text-green-600 dark:text-green-400">+8.2%</span>
                  <span className="text-xs text-gray-500 dark:text-gray-400">{t('trending.thisWeek')}</span>
                </div>
              </div>
              <div className="size-12 bg-orange-100 dark:bg-orange-900/30 rounded-lg flex items-center justify-center">
                <Users className="size-6 text-orange-600 dark:text-orange-400" />
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Main Content */}
      <Tabs defaultValue="artifacts" className="space-y-6">
        <div className="flex items-center justify-between">
          <TabsList>
            <TabsTrigger value="artifacts">
              <Flame className="mr-2 size-4" />
              {t('trending.artifacts')}
            </TabsTrigger>
            <TabsTrigger value="repositories">
              <Database className="mr-2 size-4" />
              {t('trending.repositories')}
            </TabsTrigger>
            <TabsTrigger value="growth">
              <Zap className="mr-2 size-4" />
              {t('trending.fastestGrowing')}
            </TabsTrigger>
            <TabsTrigger value="analytics">
              <BarChart3 className="mr-2 size-4" />
              {t('trending.userActivity')}
            </TabsTrigger>
          </TabsList>

          <div className="flex items-center gap-2">
            <Select value={timeRange} onValueChange={setTimeRange}>
              <SelectTrigger className="w-36">
                <Calendar className="mr-2 size-4" />
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="today">{t('trending.today')}</SelectItem>
                <SelectItem value="thisWeek">{t('trending.thisWeek')}</SelectItem>
                <SelectItem value="thisMonth">{t('trending.thisMonth')}</SelectItem>
                <SelectItem value="thisYear">{t('trending.thisYear')}</SelectItem>
                <SelectItem value="allTime">{t('trending.allTime')}</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>

        {/* Trending Artifacts Tab */}
        <TabsContent value="artifacts" className="space-y-4">
          {/* Filters */}
          <div className="flex items-center gap-3">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-gray-400" />
              <Input
                placeholder={t('artifacts.searchArtifacts')}
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>
            <Select value={formatFilter} onValueChange={setFormatFilter}>
              <SelectTrigger className="w-48">
                <Filter className="mr-2 size-4" />
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">{t('trending.allFormats')}</SelectItem>
                <SelectItem value="Maven">Maven</SelectItem>
                <SelectItem value="Docker">Docker</SelectItem>
                <SelectItem value="NPM">NPM</SelectItem>
                <SelectItem value="NuGet">NuGet</SelectItem>
                <SelectItem value="PyPI">PyPI</SelectItem>
              </SelectContent>
            </Select>
          </div>

          {/* Artifacts List */}
          <div className="grid grid-cols-1 gap-4">
            {paginatedArtifacts.map((artifact) => (
              <Card key={artifact.id} className="hover:shadow-md transition-shadow">
                <CardContent className="pt-6">
                  <div className="flex items-start gap-4">
                    {/* Rank */}
                    <div className="flex items-center justify-center w-12 flex-shrink-0">
                      {getRankBadge(artifact.rank)}
                    </div>

                    {/* Artifact Info */}
                    <div className="flex-1 min-w-0">
                      <div className="flex items-start justify-between gap-4 mb-3">
                        <div className="flex-1 min-w-0">
                          <div className="flex items-center gap-2 mb-1">
                            <h3 className="text-lg font-semibold text-gray-900 dark:text-white truncate">
                              {artifact.name}
                            </h3>
                            {artifact.rank <= 3 && (
                              <Badge className="bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-300">
                                <Flame className="mr-1 size-3" />
                                {t('trending.hot')}
                              </Badge>
                            )}
                            {artifact.growth > 20 && (
                              <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300">
                                <Zap className="mr-1 size-3" />
                                {t('trending.rising')}
                              </Badge>
                            )}
                          </div>
                          <p className="text-sm text-gray-600 dark:text-gray-400 truncate">
                            {artifact.groupId} · v{artifact.version}
                          </p>
                        </div>
                        <div className="flex items-center gap-2">
                          <Badge className={getFormatColor(artifact.format)}>
                            {artifact.format}
                          </Badge>
                        </div>
                      </div>

                      {/* Stats */}
                      <div className="grid grid-cols-2 md:grid-cols-5 gap-4 py-3 border-t border-b border-gray-200 dark:border-gray-700">
                        <div>
                          <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">{t('trending.totalDownloads')}</p>
                          <p className="text-sm font-semibold text-gray-900 dark:text-white">
                            {artifact.downloads.toLocaleString()}
                          </p>
                        </div>
                        <div>
                          <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">{t('trending.weeklyDownloads')}</p>
                          <p className="text-sm font-semibold text-gray-900 dark:text-white">
                            {artifact.weeklyDownloads.toLocaleString()}
                          </p>
                        </div>
                        <div>
                          <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">{t('trending.growth')}</p>
                          <div className="flex items-center gap-1">
                            {getTrendIcon(artifact.trend)}
                            {getTrendBadge(artifact.growth)}
                          </div>
                        </div>
                        <div>
                          <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">{t('trending.uniqueUsers')}</p>
                          <div className="flex items-center gap-1">
                            <Users className="size-3 text-gray-400" />
                            <p className="text-sm font-semibold text-gray-900 dark:text-white">
                              {artifact.uniqueUsers.toLocaleString()}
                            </p>
                          </div>
                        </div>
                        <div>
                          <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">{t('trending.stars')}</p>
                          <div className="flex items-center gap-1">
                            <Star className="size-3 text-yellow-500" />
                            <p className="text-sm font-semibold text-gray-900 dark:text-white">
                              {artifact.stars.toLocaleString()}
                            </p>
                          </div>
                        </div>
                      </div>

                      {/* Actions */}
                      <div className="flex items-center justify-between mt-3">
                        <div className="flex items-center gap-4 text-sm text-gray-600 dark:text-gray-400">
                          <span>{artifact.repository}</span>
                          <span>·</span>
                          <span>{t('trending.lastUpdated')}: {artifact.lastUpdated}</span>
                        </div>
                        <div className="flex items-center gap-2">
                          <Button variant="outline" size="sm">
                            <Eye className="mr-2 size-4" />
                            {t('trending.viewDetails')}
                          </Button>
                          <Button size="sm">
                            <Download className="mr-2 size-4" />
                            {t('trending.downloadNow')}
                          </Button>
                        </div>
                      </div>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>

          {/* Pagination */}
          <div className="flex items-center justify-between mt-4">
            <Button
              variant="outline"
              size="sm"
              disabled={artifactsPage === 1}
              onClick={() => setArtifactsPage(artifactsPage - 1)}
            >
              <ChevronLeft className="size-4" />
              {t('trending.previous')}
            </Button>
            <Button
              variant="outline"
              size="sm"
              disabled={paginatedArtifacts.length < itemsPerPage}
              onClick={() => setArtifactsPage(artifactsPage + 1)}
            >
              {t('trending.next')}
              <ChevronRight className="size-4" />
            </Button>
          </div>
        </TabsContent>

        {/* Trending Repositories Tab */}
        <TabsContent value="repositories" className="space-y-4">
          <div className="grid grid-cols-1 gap-4">
            {paginatedRepositories.map((repo) => (
              <Card key={repo.id} className="hover:shadow-md transition-shadow">
                <CardContent className="pt-6">
                  <div className="flex items-start gap-4">
                    {/* Rank */}
                    <div className="flex items-center justify-center w-12 flex-shrink-0">
                      {getRankBadge(repo.rank)}
                    </div>

                    {/* Repository Info */}
                    <div className="flex-1">
                      <div className="flex items-start justify-between gap-4 mb-3">
                        <div>
                          <div className="flex items-center gap-2 mb-1">
                            <Database className="size-5 text-blue-600 dark:text-blue-400" />
                            <h3 className="text-lg font-semibold text-gray-900 dark:text-white">
                              {repo.name}
                            </h3>
                            {repo.rank <= 3 && (
                              <Badge className="bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-300">
                                <Flame className="mr-1 size-3" />
                                {t('trending.hot')}
                              </Badge>
                            )}
                          </div>
                          <div className="flex items-center gap-2">
                            <Badge variant="outline" className="capitalize">{repo.type}</Badge>
                            <Badge className={getFormatColor(repo.format)}>{repo.format}</Badge>
                          </div>
                        </div>
                        <div className="flex items-center gap-1">
                          {getTrendIcon(repo.trend)}
                          {getTrendBadge(repo.growth)}
                        </div>
                      </div>

                      {/* Stats */}
                      <div className="grid grid-cols-2 md:grid-cols-5 gap-4 py-3 border-t border-b border-gray-200 dark:border-gray-700">
                        <div>
                          <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">{t('repositories.artifacts')}</p>
                          <p className="text-sm font-semibold text-gray-900 dark:text-white">
                            {repo.artifacts.toLocaleString()}
                          </p>
                        </div>
                        <div>
                          <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">{t('trending.totalDownloads')}</p>
                          <p className="text-sm font-semibold text-gray-900 dark:text-white">
                            {repo.downloads.toLocaleString()}
                          </p>
                        </div>
                        <div>
                          <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">{t('repositories.storageUsed')}</p>
                          <p className="text-sm font-semibold text-gray-900 dark:text-white">
                            {repo.storage}
                          </p>
                        </div>
                        <div>
                          <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">{t('trending.activeUsers')}</p>
                          <div className="flex items-center gap-1">
                            <Users className="size-3 text-gray-400" />
                            <p className="text-sm font-semibold text-gray-900 dark:text-white">
                              {repo.activeUsers.toLocaleString()}
                            </p>
                          </div>
                        </div>
                        <div>
                          <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">{t('trending.growth')}</p>
                          <p className="text-sm font-semibold text-green-600 dark:text-green-400">
                            +{repo.growth}%
                          </p>
                        </div>
                      </div>

                      {/* Actions */}
                      <div className="flex items-center justify-end gap-2 mt-3">
                        <Button variant="outline" size="sm">
                          {t('trending.viewRepository')}
                        </Button>
                      </div>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>

          {/* Pagination */}
          <div className="flex items-center justify-between mt-4">
            <Button
              variant="outline"
              size="sm"
              disabled={repositoriesPage === 1}
              onClick={() => setRepositoriesPage(repositoriesPage - 1)}
            >
              <ChevronLeft className="size-4" />
              {t('trending.previous')}
            </Button>
            <Button
              variant="outline"
              size="sm"
              disabled={paginatedRepositories.length < itemsPerPage}
              onClick={() => setRepositoriesPage(repositoriesPage + 1)}
            >
              {t('trending.next')}
              <ChevronRight className="size-4" />
            </Button>
          </div>
        </TabsContent>

        {/* Fastest Growing Tab */}
        <TabsContent value="growth" className="space-y-4">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <Card>
              <CardHeader>
                <CardTitle>{t('trending.growthRateChart')}</CardTitle>
                <CardDescription>{t('trending.last7Days')}</CardDescription>
              </CardHeader>
              <CardContent>
                <ResponsiveContainer width="100%" height={300}>
                  <BarChart data={growthRateData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                    <XAxis dataKey="name" stroke="#6b7280" />
                    <YAxis stroke="#6b7280" />
                    <Tooltip 
                      contentStyle={{ 
                        backgroundColor: 'white',
                        border: '1px solid #e5e7eb',
                        borderRadius: '8px'
                      }}
                    />
                    <Bar dataKey="growth" fill="#3b82f6" radius={[8, 8, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>{t('trending.categoryDistribution')}</CardTitle>
                <CardDescription>{t('trending.thisWeek')}</CardDescription>
              </CardHeader>
              <CardContent>
                <ResponsiveContainer width="100%" height={300}>
                  <PieChart>
                    <Pie
                      data={categoryDistributionData}
                      cx="50%"
                      cy="50%"
                      labelLine={false}
                      label={(entry) => `${entry.name} ${entry.value}%`}
                      outerRadius={100}
                      fill="#8884d8"
                      dataKey="value"
                    >
                      {categoryDistributionData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={entry.color} />
                      ))}
                    </Pie>
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>
          </div>

          {/* Top Growing Artifacts */}
          <Card>
            <CardHeader>
              <CardTitle>{t('trending.fastestGrowing')}</CardTitle>
              <CardDescription>{t('trending.last30Days')}</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                {trendingArtifacts
                  .sort((a, b) => b.growth - a.growth)
                  .slice(0, 5)
                  .map((artifact, index) => (
                    <div key={artifact.id} className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                      <div className="flex items-center gap-4">
                        <div className="flex items-center justify-center w-8 h-8 bg-blue-100 dark:bg-blue-900/30 rounded-full">
                          <span className="text-sm font-semibold text-blue-600 dark:text-blue-400">
                            {index + 1}
                          </span>
                        </div>
                        <div>
                          <p className="font-medium text-gray-900 dark:text-white">{artifact.name}</p>
                          <p className="text-sm text-gray-600 dark:text-gray-400">{artifact.groupId}</p>
                        </div>
                      </div>
                      <div className="flex items-center gap-4">
                        <Badge className={getFormatColor(artifact.format)}>
                          {artifact.format}
                        </Badge>
                        <div className="flex items-center gap-1">
                          <ArrowUpRight className="size-4 text-green-500" />
                          <span className="text-lg font-semibold text-green-600 dark:text-green-400">
                            {artifact.growth}%
                          </span>
                        </div>
                      </div>
                    </div>
                  ))}
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* Analytics Tab */}
        <TabsContent value="analytics" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>{t('trending.downloadTrendChart')}</CardTitle>
              <CardDescription>{t('trending.last7Days')}</CardDescription>
            </CardHeader>
            <CardContent>
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={downloadTrendData}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                  <XAxis dataKey="date" stroke="#6b7280" />
                  <YAxis stroke="#6b7280" />
                  <Tooltip 
                    contentStyle={{ 
                      backgroundColor: 'white',
                      border: '1px solid #e5e7eb',
                      borderRadius: '8px'
                    }}
                  />
                  <Line 
                    type="monotone" 
                    dataKey="downloads" 
                    stroke="#3b82f6" 
                    strokeWidth={2}
                    dot={{ fill: '#3b82f6', r: 4 }}
                    activeDot={{ r: 6 }}
                  />
                </LineChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <Card>
              <CardHeader>
                <CardTitle>{t('trending.topArtifacts')}</CardTitle>
                <CardDescription>{t('trending.thisWeek')}</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  {trendingArtifacts.slice(0, 5).map((artifact) => (
                    <div key={artifact.id} className="flex items-center justify-between">
                      <div className="flex items-center gap-3">
                        {getRankBadge(artifact.rank)}
                        <div>
                          <p className="font-medium text-gray-900 dark:text-white text-sm">
                            {artifact.name}
                          </p>
                          <p className="text-xs text-gray-600 dark:text-gray-400">
                            {artifact.weeklyDownloads.toLocaleString()} {t('trending.downloads')}
                          </p>
                        </div>
                      </div>
                      <Badge className={getFormatColor(artifact.format)}>
                        {artifact.format}
                      </Badge>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>{t('trending.mostActive')}</CardTitle>
                <CardDescription>{t('trending.thisWeek')}</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  {trendingArtifacts
                    .sort((a, b) => b.uniqueUsers - a.uniqueUsers)
                    .slice(0, 5)
                    .map((artifact, index) => (
                      <div key={artifact.id} className="flex items-center justify-between">
                        <div className="flex items-center gap-3">
                          <div className="flex items-center justify-center w-6 h-6 bg-gray-100 dark:bg-gray-800 rounded">
                            <span className="text-xs font-medium text-gray-600 dark:text-gray-400">
                              {index + 1}
                            </span>
                          </div>
                          <div>
                            <p className="font-medium text-gray-900 dark:text-white text-sm">
                              {artifact.name}
                            </p>
                            <div className="flex items-center gap-1 text-xs text-gray-600 dark:text-gray-400">
                              <Users className="size-3" />
                              <span>{artifact.uniqueUsers.toLocaleString()} {t('trending.uniqueUsers')}</span>
                            </div>
                          </div>
                        </div>
                        <Badge className={getFormatColor(artifact.format)}>
                          {artifact.format}
                        </Badge>
                      </div>
                    ))}
                </div>
              </CardContent>
            </Card>
          </div>
        </TabsContent>
      </Tabs>
    </div>
  );
}