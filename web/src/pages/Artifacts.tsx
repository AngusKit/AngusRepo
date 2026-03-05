import { useState } from 'react';
import { Search, Download, Package, Calendar, FileText, Database, Tag, MoreVertical, Trash2, Eye, Copy, Filter, GitBranch, FolderOpen, HardDrive, ChevronRight, ExternalLink } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { useLanguage } from '@/components/LanguageProvider';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from '@/components/ui/dropdown-menu';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { toast } from 'sonner';
import { copyToClipboard } from '@/utils/clipboard';

export function Artifacts() {
  const { t } = useLanguage();
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedRepository, setSelectedRepository] = useState('all');
  const [selectedFormat, setSelectedFormat] = useState('all');
  const [selectedArtifact, setSelectedArtifact] = useState<any>(null);
  const [detailsOpen, setDetailsOpen] = useState(false);

  const artifacts = [
    {
      id: '1',
      name: 'spring-boot-starter-web',
      groupId: 'org.springframework.boot',
      artifactId: 'spring-boot-starter-web',
      version: '3.1.5',
      format: 'Maven',
      repository: 'maven-releases',
      size: '12.4 MB',
      checksum: 'sha256:a3c5f1...',
      uploadedBy: 'Alex Chen',
      uploadedAt: '2024-01-15 10:30:00',
      downloads: 1245,
      packaging: 'jar',
    },
    {
      id: '2',
      name: 'nginx',
      tag: '1.21-alpine',
      digest: 'sha256:abc123...',
      format: 'Docker',
      repository: 'docker-registry',
      size: '142 MB',
      uploadedBy: 'Sarah Johnson',
      uploadedAt: '2024-01-14 15:20:00',
      downloads: 567,
      architecture: 'amd64',
      os: 'linux',
    },
    {
      id: '3',
      name: 'react',
      version: '18.2.0',
      format: 'NPM',
      repository: 'npm-private',
      size: '2.8 MB',
      uploadedBy: 'Mike Wilson',
      uploadedAt: '2024-01-13 09:15:00',
      downloads: 2341,
      license: 'MIT',
      homepage: 'https://react.dev',
    },
    {
      id: '4',
      name: 'pandas',
      version: '2.1.4',
      format: 'PyPI',
      repository: 'pypi-public',
      size: '18.5 MB',
      uploadedBy: 'Emily Brown',
      uploadedAt: '2024-01-12 14:45:00',
      downloads: 892,
      license: 'BSD',
    },
    {
      id: '5',
      name: 'EntityFramework',
      version: '6.4.4',
      format: 'NuGet',
      repository: 'nuget-public',
      size: '5.2 MB',
      uploadedBy: 'Tom Anderson',
      uploadedAt: '2024-01-11 11:30:00',
      downloads: 456,
      license: 'Apache-2.0',
    },
  ];

  const formatBadgeColor = (format: string) => {
    const colors: Record<string, string> = {
      'Maven': 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-300',
      'Docker': 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300',
      'NPM': 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300',
      'PyPI': 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-300',
      'NuGet': 'bg-indigo-100 text-indigo-700 dark:bg-indigo-900/30 dark:text-indigo-300',
    };
    return colors[format] || 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-300';
  };

  const filteredArtifacts = artifacts.filter(artifact => {
    const matchesSearch = artifact.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         (artifact.groupId && artifact.groupId.toLowerCase().includes(searchQuery.toLowerCase()));
    const matchesRepository = selectedRepository === 'all' || artifact.repository === selectedRepository;
    const matchesFormat = selectedFormat === 'all' || artifact.format === selectedFormat;
    return matchesSearch && matchesRepository && matchesFormat;
  });

  const handleViewDetails = (artifact: any) => {
    setSelectedArtifact(artifact);
    setDetailsOpen(true);
  };

  const handleDownload = (artifact: any) => {
    toast.success(`Downloading ${artifact.name}...`);
  };

  const handleCopy = async (text: string) => {
    const success = await copyToClipboard(text);
    if (success) {
      toast.success(t('artifacts.urlCopied'));
    } else {
      toast.error('Failed to copy');
    }
  };

  const handleDelete = (artifact: any) => {
    toast.success(t('artifacts.artifactDeleted'));
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl text-gray-900 dark:text-white mb-2">{t('artifacts.title')}</h1>
        <p className="text-gray-600 dark:text-gray-400">{t('artifacts.description')}</p>
      </div>

      {/* Search and Filters */}
      <div className="flex flex-col sm:flex-row gap-4">
        <div className="flex-1 relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-gray-400" />
          <Input
            placeholder={t('artifacts.searchArtifacts')}
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="pl-10"
          />
        </div>
        <Select value={selectedRepository} onValueChange={setSelectedRepository}>
          <SelectTrigger className="w-full sm:w-48">
            <SelectValue placeholder="Repository" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">All Repositories</SelectItem>
            <SelectItem value="maven-releases">maven-releases</SelectItem>
            <SelectItem value="docker-registry">docker-registry</SelectItem>
            <SelectItem value="npm-private">npm-private</SelectItem>
            <SelectItem value="pypi-public">pypi-public</SelectItem>
            <SelectItem value="nuget-public">nuget-public</SelectItem>
          </SelectContent>
        </Select>
        <Select value={selectedFormat} onValueChange={setSelectedFormat}>
          <SelectTrigger className="w-full sm:w-40">
            <SelectValue placeholder="Format" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">All Formats</SelectItem>
            <SelectItem value="Maven">Maven</SelectItem>
            <SelectItem value="Docker">Docker</SelectItem>
            <SelectItem value="NPM">NPM</SelectItem>
            <SelectItem value="PyPI">PyPI</SelectItem>
            <SelectItem value="NuGet">NuGet</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* Artifacts List */}
      <Card>
        <CardContent className="p-0">
          <div className="divide-y divide-gray-200 dark:divide-gray-700">
            {filteredArtifacts.map((artifact) => (
              <div
                key={artifact.id}
                className="p-4 hover:bg-gray-50 dark:hover:bg-gray-800/50 transition-colors cursor-pointer"
                onClick={() => handleViewDetails(artifact)}
              >
                <div className="flex items-start justify-between gap-4">
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 mb-2">
                      <Package className="size-5 text-gray-600 dark:text-gray-400 flex-shrink-0" />
                      <h3 className="text-base text-gray-900 dark:text-white font-medium truncate">
                        {artifact.name}
                      </h3>
                      <Badge className={formatBadgeColor(artifact.format)}>{artifact.format}</Badge>
                      {artifact.license && (
                        <Badge variant="outline" className="text-xs">{artifact.license}</Badge>
                      )}
                    </div>
                    {artifact.groupId && (
                      <p className="text-sm text-gray-600 dark:text-gray-400 font-mono mb-1">
                        {artifact.groupId}:{artifact.artifactId}
                      </p>
                    )}
                    <div className="flex flex-wrap items-center gap-4 text-sm text-gray-600 dark:text-gray-400">
                      <span className="flex items-center gap-1">
                        <FileText className="size-4" />
                        Version: <span className="font-medium">{artifact.version || artifact.tag}</span>
                      </span>
                      <span className="flex items-center gap-1">
                        <HardDrive className="size-4" />
                        {artifact.size}
                      </span>
                      <span className="flex items-center gap-1">
                        <Download className="size-4" />
                        {artifact.downloads} downloads
                      </span>
                      <span className="flex items-center gap-1">
                        <Calendar className="size-4" />
                        {new Date(artifact.uploadedAt).toLocaleDateString()}
                      </span>
                    </div>
                  </div>
                  <div className="flex items-center gap-2" onClick={(e) => e.stopPropagation()}>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handleDownload(artifact)}
                    >
                      <Download className="size-4" />
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handleViewDetails(artifact)}
                    >
                      <ChevronRight className="size-4" />
                    </Button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>

      {filteredArtifacts.length === 0 && (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-12">
            <Package className="size-12 text-gray-400 mb-4" />
            <h3 className="text-lg text-gray-900 dark:text-white mb-2">{t('artifacts.noArtifacts')}</h3>
            <p className="text-gray-600 dark:text-gray-400">{t('artifacts.noSearchResults')}</p>
          </CardContent>
        </Card>
      )}

      {/* Artifact Details Dialog */}
      <Dialog open={detailsOpen} onOpenChange={setDetailsOpen}>
        <DialogContent className="max-w-3xl max-h-[80vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>{t('artifacts.artifactDetails')}</DialogTitle>
            <DialogDescription>
              Complete information about this artifact
            </DialogDescription>
          </DialogHeader>
          {selectedArtifact && (
            <Tabs defaultValue="overview" className="mt-4">
              <TabsList>
                <TabsTrigger value="overview">Overview</TabsTrigger>
                <TabsTrigger value="versions">Versions</TabsTrigger>
                <TabsTrigger value="dependencies">Dependencies</TabsTrigger>
              </TabsList>
              <TabsContent value="overview" className="space-y-4 mt-4">
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('common.name')}</p>
                    <p className="text-gray-900 dark:text-white font-medium">{selectedArtifact.name}</p>
                  </div>
                  {selectedArtifact.groupId && (
                    <>
                      <div>
                        <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('artifacts.groupId')}</p>
                        <p className="text-gray-900 dark:text-white font-mono text-sm">{selectedArtifact.groupId}</p>
                      </div>
                      <div>
                        <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('artifacts.artifactId')}</p>
                        <p className="text-gray-900 dark:text-white font-mono text-sm">{selectedArtifact.artifactId}</p>
                      </div>
                    </>
                  )}
                  <div>
                    <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('common.version')}</p>
                    <p className="text-gray-900 dark:text-white font-medium">
                      {selectedArtifact.version || selectedArtifact.tag}
                    </p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('common.format')}</p>
                    <Badge className={formatBadgeColor(selectedArtifact.format)}>{selectedArtifact.format}</Badge>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">Repository</p>
                    <p className="text-gray-900 dark:text-white">{selectedArtifact.repository}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('common.size')}</p>
                    <p className="text-gray-900 dark:text-white">{selectedArtifact.size}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('artifacts.uploadedBy')}</p>
                    <p className="text-gray-900 dark:text-white">{selectedArtifact.uploadedBy}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('artifacts.uploadedAt')}</p>
                    <p className="text-gray-900 dark:text-white">{selectedArtifact.uploadedAt}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('artifacts.downloadCount')}</p>
                    <p className="text-gray-900 dark:text-white">{selectedArtifact.downloads}</p>
                  </div>
                  {selectedArtifact.license && (
                    <div>
                      <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('artifacts.license')}</p>
                      <Badge variant="outline">{selectedArtifact.license}</Badge>
                    </div>
                  )}
                  {selectedArtifact.checksum && (
                    <div className="col-span-2">
                      <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('artifacts.checksum')}</p>
                      <div className="flex items-center gap-2">
                        <code className="text-xs bg-gray-100 dark:bg-gray-800 px-2 py-1 rounded">
                          {selectedArtifact.checksum}
                        </code>
                        <Button variant="ghost" size="sm" onClick={() => handleCopy(selectedArtifact.checksum)}>
                          <Copy className="size-3" />
                        </Button>
                      </div>
                    </div>
                  )}
                </div>
                <div className="flex gap-2 pt-4 border-t border-gray-200 dark:border-gray-700">
                  <Button onClick={() => handleDownload(selectedArtifact)}>
                    <Download className="mr-2 size-4" />
                    {t('common.download')}
                  </Button>
                  <Button variant="outline">
                    <ExternalLink className="mr-2 size-4" />
                    Open Repository
                  </Button>
                  <Button variant="outline" className="text-red-600 dark:text-red-400" onClick={() => handleDelete(selectedArtifact)}>
                    <Trash2 className="mr-2 size-4" />
                    {t('common.delete')}
                  </Button>
                </div>
              </TabsContent>
              <TabsContent value="versions" className="mt-4">
                <div className="space-y-2">
                  {[selectedArtifact.version || selectedArtifact.tag, '3.1.4', '3.1.3', '3.1.2', '3.1.1'].map((version, index) => (
                    <div key={index} className="flex items-center justify-between p-3 rounded-lg border border-gray-200 dark:border-gray-700">
                      <span className="font-mono text-sm text-gray-900 dark:text-white">{version}</span>
                      {index === 0 && <Badge>Latest</Badge>}
                      <Button variant="outline" size="sm">
                        <Download className="size-4" />
                      </Button>
                    </div>
                  ))}
                </div>
              </TabsContent>
              <TabsContent value="dependencies" className="mt-4">
                <p className="text-gray-600 dark:text-gray-400 text-sm">
                  Dependencies information will be displayed here
                </p>
              </TabsContent>
            </Tabs>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}